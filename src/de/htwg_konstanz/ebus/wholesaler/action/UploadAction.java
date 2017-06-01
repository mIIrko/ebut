package de.htwg_konstanz.ebus.wholesaler.action;

import de.htwg_konstanz.ebus.wholesaler.demo.IAction;
import de.htwg_konstanz.ebus.wholesaler.demo.util.Constants;
import de.htwg_konstanz.ebus.wholesaler.main.ImportUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * @author mirko bay
 */
public class UploadAction implements IAction {

    public UploadAction() {
        super();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, ArrayList<String> errorList, ArrayList<String> infoList) {

        if (ImportUtil.importXmlFile(request, errorList)) {
            //File uploaded successfully
            infoList.add("succesfull import - processing the file");
        }

        return "import.jsp";
    }

    @Override
    public boolean accepts(String actionName) {
        return actionName.equalsIgnoreCase(Constants.ACTION_UPLOAD);
    }

}
