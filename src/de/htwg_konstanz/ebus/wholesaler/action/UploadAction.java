package de.htwg_konstanz.ebus.wholesaler.action;

import de.htwg_konstanz.ebus.wholesaler.demo.IAction;
import de.htwg_konstanz.ebus.wholesaler.demo.util.Constants;
import de.htwg_konstanz.ebus.wholesaler.main.ImportUtil;
import de.htwg_konstanz.ebus.wholesaler.main.Role;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mirko bay
 */
public class UploadAction implements IAction {

    private Role role = null;


    public UploadAction() {
        super();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, ArrayList<String> errorList, ArrayList<String> infoList) {

        InputStream uploadedStream = getInputStreamFromRequest(request, errorList, infoList);
        Document doc = null;
        try {
            doc = ImportUtil.newDocumentFromInputStream(uploadedStream);
        } catch (SAXException se) {
            errorList.add("document is not well formed");
        } catch (IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }


        if (ImportUtil.importXmlFile(role, doc, errorList, infoList)) {
            //File uploaded successfully
            infoList.add("All articles imported successful ");
        }

        return "import.jsp";
    }

    @Override
    public boolean accepts(String actionName) {
        return actionName.equalsIgnoreCase(Constants.ACTION_UPLOAD);
    }


    private InputStream getInputStreamFromRequest(HttpServletRequest request,
                                                  ArrayList<String> errorList,
                                                  ArrayList<String> infoList) {


        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Set factory constraints
        // factory.setSizeThreshold(yourMaxMemorySize); in bytes
        // factory.setRepository(yourTempDirectory); use system default

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        // Set overall request size constraint
        // upload.setSizeMax(yourMaxRequestSize);

        // Parse the request
        List<FileItem> items = null;
        try {
            items = upload.parseRequest(request);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }

        System.out.println("NUMBER OF UPLOADED FILES = " + items.size());

        InputStream stream = null;

        // Process the uploaded items
        for (FileItem item : items) {

            if (item.isFormField()) {
                // processing a simple html form filed, e.g. for the
                // file name or the uploader
                String name = item.getFieldName();
                String value = item.getString();
                if (name.equals("role")) {
                    int roleNumb = Integer.parseInt(value);
                    role = Role.getRoleByNumber(roleNumb);
                }
            } else {
                // processing the file
                String contentType = item.getContentType();
                long sizeInBytes = item.getSize();

                // no file selected
                if (sizeInBytes == 0) {
                    errorList.add("No file selected for upload");
                    return null;
                }

                // when its not a xml file
                if (!contentType.equals("text/xml")) {
                    errorList.add("Wrong file type - just .xml files accepted");
                    return null;
                }

                System.out.println("-- FILE IMPORT --");
                System.out.println("file size >" + sizeInBytes + "<");
                System.out.println("file content type >" + contentType + "<");

                try {
                    stream = item.getInputStream();
                } catch (IOException e) {
                    errorList.add("Can't get inputstream from uploaded file");
                    return null;
                }
            }
        }

        return stream;

    }
}
