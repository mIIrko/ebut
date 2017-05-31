package de.htwg_konstanz.ebus.wholesaler.action;

import de.htwg_konstanz.ebus.wholesaler.demo.IAction;
import de.htwg_konstanz.ebus.wholesaler.demo.util.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

public class ImportAction implements IAction {

    public ImportAction()
    {
        super();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, ArrayList<String> errorList, ArrayList<String> infoList) {
        return "import.jsp";
    }

    @Override
    public boolean accepts(String actionName) {
        return actionName.equalsIgnoreCase(Constants.ACTION_IMPORT);
    }
}
