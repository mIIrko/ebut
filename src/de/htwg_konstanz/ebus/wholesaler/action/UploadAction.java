package de.htwg_konstanz.ebus.wholesaler.action;

import de.htwg_konstanz.ebus.wholesaler.demo.IAction;
import de.htwg_konstanz.ebus.wholesaler.demo.util.Constants;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author mirko bay
 */
public class UploadAction implements IAction {

    public UploadAction() {
        super();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, ArrayList<String> errorList, ArrayList<String> infoList) {

        int role = -1;

        /*
         * File Upload
         * with org.apache.commons.fileupload
         *
         * https://commons.apache.org/proper/commons-fileupload/using.html
         */

        //process only if its multipart content
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
                        // for processing a simple html form filed, e.g. for the file name or the uploader
                        String name = item.getFieldName();
                        String value = item.getString();
                        if (name.equals("role")) {
                            role = Integer.parseInt(value);
                        }

                    } else {
                        // processing the file
                        String fieldName = item.getFieldName();
                        String fileName = item.getName();
                        String contentType = item.getContentType();
                        boolean isInMemory = item.isInMemory();
                        long sizeInBytes = item.getSize();


                        if (!contentType.equals("text/xml")) {

                            if (sizeInBytes == 0) {
                                errorList.add("No file chosen for upload");

                            } else {
                                errorList.add("Wrong file type - just .xml files accepted");
                            }
                            System.out.println("-- FILE IMPORT --");
                            System.out.println("file size >" + sizeInBytes + "<");
                            System.out.println("file content type >" + contentType + "<");

                            return "import.jsp";


                        }
                        InputStream uploadedStream = item.getInputStream();

                        //https://stackoverflow.com/a/2345924
                        int size = 0;
                        byte[] buffer = new byte[1024];
                        while ((size = uploadedStream.read(buffer)) != -1) System.out.write(buffer, 0, size);

                        uploadedStream.close();

                        // byte[] data = item.get();

                    }
                }

            } catch (FileUploadException fue) {
                fue.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }


            //File uploaded successfully
            infoList.add("succesfull import - processing the file");

        } else {
            errorList.add("file is not a multipart content");
        }

        return "import.jsp";

    }

    @Override
    public boolean accepts(String actionName) {
        return actionName.equalsIgnoreCase(Constants.ACTION_UPLOAD);
    }

}
