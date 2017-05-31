package de.htwg_konstanz.ebus.wholesaler.action;

import com.sun.xml.xsom.impl.Const;
import de.htwg_konstanz.ebus.wholesaler.demo.IAction;
import de.htwg_konstanz.ebus.wholesaler.demo.util.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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


        String searchTerm = request.getParameter("searchterm");
        int role = Integer.parseInt(request.getParameter("role"));
        System.out.println("Role from user = " + role);
        if (searchTerm.equals("")) {
            System.out.println("no search term specified");

            // TODO: here we call the service to retrieve the xml file (as stream or file)
            // exportCatalogXML(String searchTerm, int role); -> empty search term means all
        } else {
            System.out.println("the search term is " + searchTerm);

        }

        // https://stackoverflow.com/a/14281064
        // tell the browser the file type were going to send
        response.setContentType("text/xml");

        // show the download dialog
        response.setHeader("Content-disposition","attachment; filename=test.xml");

        try {
            OutputStream out = response.getOutputStream();
            FileInputStream in = new FileInputStream(createDummyFile());
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.flush();
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
