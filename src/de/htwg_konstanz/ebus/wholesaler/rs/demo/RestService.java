package de.htwg_konstanz.ebus.wholesaler.rs.demo;

import de.htwg_konstanz.ebus.framework.wholesaler.api.boa._BaseBOA;
import de.htwg_konstanz.ebus.wholesaler.main.ExportUtil;
import de.htwg_konstanz.ebus.wholesaler.main.ImportUtil;
import de.htwg_konstanz.ebus.wholesaler.main.Role;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.ArrayList;

/**
 * @author mirko bay, 2017-06-20
 */

@Path("/service")
public class RestService {

    /**
     * the POST=CREATE method for the import
     * https://crispcode.wordpress.com/2012/07/27/jersey-rest-web-service-to-upload-multiple-files/
     *
     * @param uploadedStream
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response importService(@Context HttpHeaders headers, InputStream uploadedStream) {

        if (!headers.getRequestHeaders().containsKey("content-length") || !headers.getRequestHeaders().containsKey("Content-Length")) {
            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity("header not complete -> content length expected")
                .type(MediaType.APPLICATION_XML)
                .build();
        }

        System.out.println(headers.getRequestHeader("Content-Length").toString());
        System.out.println(headers.getRequestHeader("Content-Length").size());
        System.out.println(headers.getRequestHeader("Content-Length").get(0));

        if (headers.getRequestHeader("Content-Length").size() > 1) {
            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity("mutiple file upload not supported")
                .type(MediaType.APPLICATION_XML)
                .build();
        }

        if (headers.getRequestHeader("Content-Length").get(0).equals("0")) {
            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity("no file selected")
                .type(MediaType.APPLICATION_XML)
                .build();
        }

        ArrayList<String> errorList = new ArrayList<>();
        ArrayList<String> infoList = new ArrayList<>();

        Role role = Role.SUPPLIER_ROLE;
        Document doc = null;

        try {
            doc = ImportUtil.newDocumentFromInputStream(uploadedStream);
        } catch (SAXException se) {
            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity("document is not well formed: " + se.getMessage().split(": ")[0])
                .type(MediaType.APPLICATION_XML)
                .build();
        } catch (IOException | ParserConfigurationException e) {
            System.out.println("e : MESSAGE > " + e.getMessage());
        }

        if (ImportUtil.importXmlFile(role, doc, errorList, infoList)) {

            // ensure that all the performed changes in the previous action will be committed to the database
            _BaseBOA.getInstance().commit();

            // correct response status is 201 (created)
            return Response.status(200).entity(convertListToString(infoList)).build();
        } else {

            // ensure that all the performed changes in the previous action will be committed to the database
            _BaseBOA.getInstance().commit();

            // todo: correct response status is 409 (conflict), if article already exists
            return Response
                .status(404)
                .entity(convertListToString(errorList))
                .type(MediaType.TEXT_XML)
                .build();
        }
    }

    /**
     * the GET=READ method for the export
     *
     * @param request
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response exportServiceXML(@Context HttpHeaders headers, @Context HttpServletRequest request, @QueryParam("term") String term) {

        if (!(headers.getAcceptableMediaTypes().contains(MediaType.APPLICATION_XML_TYPE))) {
            return Response.status(Response.Status.BAD_REQUEST).entity("<error><msg>specify accept header</msg></error>").build();
        }

        String searchTerm = "";
        File exportFile = null;
        Role role = Role.SUPPLIER_ROLE;
        boolean matchExact = false;

        if (term != null) {
            // export selective
            searchTerm = term;
        }

        try {
            exportFile = ExportUtil.exportCatalogXML(searchTerm, matchExact, role);
        } catch (IOException | TransformerException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("<msg>errors while creating the output file</msg>").build();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("<msg>" + e.getMessage() + "</msg>").build();
        }

        return Response.status(Response.Status.OK).entity(convertFileToString(exportFile)).type(MediaType.APPLICATION_XML).build();

    }

    /**
     * the GET=READ method for the export
     *
     * @param request
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response exportServiceXHTML(@Context HttpHeaders headers, @Context HttpServletRequest request, @QueryParam("term") String term) {

        if (!(headers.getAcceptableMediaTypes().contains(MediaType.APPLICATION_XHTML_XML_TYPE))) {
            return Response.status(Response.Status.BAD_REQUEST).entity("<error><msg>specify accept header</msg></error>").build();
        }

        String searchTerm = "";
        File exportFile = null;
        Role role = Role.SUPPLIER_ROLE;
        boolean matchExact = false;


        if (term != null) {
            // export selective
            searchTerm = term;
        }

        try {
            exportFile = ExportUtil.exportCatalogXHTML(searchTerm, matchExact, role);
        } catch (IOException | TransformerException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("<msg>errors while creating the output file</msg>").build();

        } catch (RuntimeException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("<msg>" + e.getMessage() + "</msg>").build();
        }

        System.out.println(convertFileToString(exportFile));

        // must be xhtml (checked above)
        return Response.status(Response.Status.OK).entity(convertFileToString(exportFile)).type(MediaType.APPLICATION_XHTML_XML).build();

    }


    private String convertListToString(ArrayList<String> list) {
        StringBuilder stringBuilder = new StringBuilder("");
        for (String info : list) {
            stringBuilder.append(info);
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    private String convertFileToString(File file) {
        StringBuffer fileContents = new StringBuffer();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            while (line != null) {
                fileContents.append(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            System.err.println("Error converting file object to string");
            e.printStackTrace();
        }

        return fileContents.toString();
    }
}
