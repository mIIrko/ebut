package de.htwg_konstanz.ebus.wholesaler.main;

import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by mirko on 01.06.17.
 */
public class ExportUtil {

    public static File convertDocToFile(Document doc) throws IOException,TransformerException {
        DOMSource source = new DOMSource(doc);
        File file = new File("/tmp/output.xml");
        FileWriter writer = new FileWriter(file);
        StreamResult result = new StreamResult(writer);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(source, result);

        return file;
    }
    public static Document exportCatalogXML(String searchTerm, boolean matchExact, int role) {

        // TODO implement!

        return null;

    }
}
