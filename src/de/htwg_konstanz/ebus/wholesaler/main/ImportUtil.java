package de.htwg_konstanz.ebus.wholesaler.main;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mirko 
 * 
 * Created on 01.06.17.
 */
public class ImportUtil {

    /**
     * File Upload
     * with org.apache.commons.fileupload
     * <p>
     * https://commons.apache.org/proper/commons-fileupload/using.html
     *
     * @param request
     * @param errorList
     */
    public static boolean importXmlFile(HttpServletRequest request, ArrayList<String> errorList) {

        int role = -1;

        //checks whether there is a file upload request or not
        if (ServletFileUpload.isMultipartContent(request)) {

            try {
                // Create a factory for disk-based file items
                DiskFileItemFactory factory = new DiskFileItemFactory();

                // Set factory constraints
                //factory.setSizeThreshold(yourMaxMemorySize); in bytes
                //factory.setRepository(yourTempDirectory); use system default

                // Create a new file upload handler
                ServletFileUpload upload = new ServletFileUpload(factory);

                // Set overall request size constraint
                //upload.setSizeMax(yourMaxRequestSize);

                // Parse the request
                List<FileItem> items = upload.parseRequest(request);

                // Process the uploaded items
                for (FileItem item : items) {

                    if (item.isFormField()) {
                        // processing a simple html form filed, e.g. for the file name or the uploader
                        String name = item.getFieldName();
                        String value = item.getString();
                        if (name.equals("role")) {
                            role = Integer.parseInt(value);
                        }
                    } else {
                        // processing the file
                        String contentType = item.getContentType();
                        long sizeInBytes = item.getSize();

                        // no file selected
                        if (sizeInBytes == 0) {
                            errorList.add("No file selected for upload");
                            return false;
                        }

                        // when its not a xml file
                        if (!contentType.equals("text/xml")) {
                            errorList.add("Wrong file type - just .xml files accepted");
                            return false;
                        }

                        System.out.println("-- FILE IMPORT --");
                        System.out.println("file size >" + sizeInBytes + "<");
                        System.out.println("file content type >" + contentType + "<");

                        InputStream uploadedStream = item.getInputStream();
                        Document doc = null;

                        // parsing stream to doc and check if its well formed
                        try {
                            doc = newDocumentFromInputStream(uploadedStream);
                        } catch (SAXException se) {
                            errorList.add("document is not well formed");
                            return false;
                        } catch (IOException | ParserConfigurationException e) {
                            e.printStackTrace();
                        }

                        // check if the document is valid
                        if (!validateXmlAgainstBmeCat(doc)) {
                            errorList.add("document is not valid");
                            return false;
                        }

                        // sysout of the stream
                        //https://stackoverflow.com/a/2345924
                        int size = 0;
                        byte[] buffer = new byte[1024];
                        while ((size = uploadedStream.read(buffer)) != -1) System.out.write(buffer, 0, size);

                        uploadedStream.close();
                    }
                }
            } catch (FileUploadException | IOException e) {
                e.printStackTrace();
            }
        } else {
            errorList.add("file is not a multipart content");
            return false;
        }

        return true;
    }

    /**
     * http://www.java2s.com/Code/Java/XML/NewDocumentFromInputStream.htm
     * <p>
     * if the parsing to document failed, the xml file is not well formed
     *
     * @param in The InputStream from the upload
     * @return Document the created document
     */
    private static Document newDocumentFromInputStream(InputStream in) throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = null;
        DocumentBuilder builder = null;
        Document ret = null;

        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();

        return builder.parse(new InputSource(in));
    }

    /**
     *
     * https://docs.oracle.com/javase/7/docs/api/javax/xml/validation/package-summary.html
     *
     * @param document The document to validate
     * @return boolean true, if the document is valid
     */
    private static boolean validateXmlAgainstBmeCat(Document document) {

        // create a SchemaFactory capable of understanding WXS schemas
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // load a WXS schema, represented by a Schema instance
        Source schemaFile = new StreamSource(new File("/wsdl/bmecat_new_catalog_1_2_simple_eps_V0.96.xsd"));
        Schema schema = null;
        try {
            schema = factory.newSchema(schemaFile);
        } catch (SAXException se) {
            System.err.println("DAS XML SCHEMA IST NICHT VALIDE!");
            se.printStackTrace();
        }

        // create a Validator instance, which can be used to validate an instance document
        Validator validator = schema.newValidator();

        // validate the DOM tree
        try {
            validator.validate(new DOMSource(document));
        } catch (SAXException e) {
            // instance document is invalid!
            return false;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return true;
    }
}
