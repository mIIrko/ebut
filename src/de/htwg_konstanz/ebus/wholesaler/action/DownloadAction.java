package de.htwg_konstanz.ebus.wholesaler.action;

import de.htwg_konstanz.ebus.wholesaler.demo.IAction;
import de.htwg_konstanz.ebus.wholesaler.demo.util.Constants;
import de.htwg_konstanz.ebus.wholesaler.main.ExportUtil;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by mirko on 31.05.17.
 */
public class DownloadAction implements IAction {

    public DownloadAction() {
        super();
    }


    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, ArrayList<String> errorList, ArrayList<String> infoList) {

        // get the values from the request
        String searchTerm = request.getParameter("searchterm");
        boolean matchExact = request.getParameter("searchterm").equals("true");
        int role = Integer.parseInt(request.getParameter("role"));
        String requestedFormat = request.getParameter("type");

        System.out.println("-- FILE DOWNLOAD REQUEST --");
        System.out.println("User Role >" + role + "<");
        System.out.println("Search term >" + searchTerm + "<");
        System.out.println("Match exact >" + matchExact + "<");

        Document doc = null;

        if (requestedFormat.equals("xml")) {
            // TODO: here we call the service to retrieve the XML file (as stream or file)
            // doc = exportCatalogXML(String searchTerm, matchExact, int role); -> empty search term means all
        } else if (requestedFormat.equals("xhtml")) {
            // TODO: here we call the service to retrieve the XHTML file (as stream or file)
            // doc = exportCatalogXHTML(String searchTerm, matchExact, int role); -> empty search term means all
        } else {
            // this case is never chosen with the "normal" request from the input form
            errorList.add("requested file format (" + requestedFormat + ") is not available" );
            return "export.jsp";
        }

        try {
            ExportUtil.convertDocToFile(doc);
        } catch (IOException ioe) {
            errorList.add("cant create output file");
        } catch (TransformerException te) {
            errorList.add("transformation of doc to file failed");
        }


        try {
           sendFile(response, createDummyFile(), requestedFormat);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return "export.jsp";
    }

    @Override
    public boolean accepts(String actionName) {
        return actionName.equalsIgnoreCase(Constants.ACTION_DOWNLOAD);
    }


    /**
     * sends a file to the browser
     * can also be implemented with a stream
     *
     * @param response
     * @param file
     * @throws IOException
     */
    private void sendFile(HttpServletResponse response, File file, String contentType) throws IOException{

        // tell the browser the file type were going to send and show the download dialog
        if (contentType.equals("xml")) {
            // https://www.ietf.org/rfc/rfc2376.txt
            response.setContentType("text/xml");
            response.setHeader("Content-disposition","attachment; filename=test.xml");
        } else if(contentType.equals("xhtml")) {
            // https://www.w3.org/TR/xhtml-media-types/#application-xhtml-xml
            response.setContentType("application/xhtml+xml");
            response.setHeader("Content-disposition","attachment; filename=test.xhtml");
        } else {
            response.setContentType("text/plain");
            response.setHeader("Content-disposition","attachment; filename=test.txt");
        }


        OutputStream out = response.getOutputStream();
        FileInputStream in = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int length;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        in.close();
        out.flush();
    }


    /**
     * Creates a dummy file object for testing purposes
     *
     * @return a dummy File object
     * @throws IOException
     */
    private File createDummyFile() throws IOException {
        File file = new File("tmp.xml");
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        while (file.length() <= 400) {
            writer.write("<test><nocheiner>Haaaaaallooooo</nocheiner></test>");
            writer.write("\n");
        }
        writer.flush();
        writer.close();

        return file;
    }


}
