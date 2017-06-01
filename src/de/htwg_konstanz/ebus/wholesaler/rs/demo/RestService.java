package de.htwg_konstanz.ebus.wholesaler.rs.demo;

import de.htwg_konstanz.ebus.wholesaler.main.ImportUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

/**
 * Created by mirko on 01.06.17.
 */

@Path("/rest")
public class RestService {

    //https://crispcode.wordpress.com/2012/07/27/jersey-rest-web-service-to-upload-multiple-files/

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("text/plain")
    @Path("/upload")
    public Response registerWebService(@Context HttpServletRequest request) {

        String responseStatus = "success";
        ArrayList<String> errorList = new ArrayList<>();

        if (ImportUtil.importXmlFile(request, errorList)) {
            return Response.status(200).build();

        } else {
            StringBuilder errorStringBuilder = new StringBuilder("");
            for (String err : errorList) {
                errorStringBuilder.append(err);
                errorStringBuilder.append("\n");
            }
            // TODO set correct response status
            return Response.status(200).entity(errorStringBuilder.toString()).build();

        }


    }
}
