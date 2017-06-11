package de.htwg_konstanz.ebus.wholesaler.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

/**
 * @author mirko bay
 * 2017-06-01
 *
 * Class for retrieving and transforming the data;
 * throws all exceptions to the actions
 *
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
    public static File exportCatalogXML(String searchTerm, boolean matchExact, int roleNumb) throws IOException, TransformerException{

        Role role = Role.getRoleByNumber(roleNumb);
        ExportManagerImpl manager = new ExportManagerImpl(role);
        Document doc;
        if (searchTerm.equals("")) {
            doc = manager.retriveAllArticles();
        } else {
            doc = manager.retriveSelectiveArticles(searchTerm);
        }

        return convertDocToFile(doc);

    }

    public static File exportCatalogXHTML(String searchTerm, boolean matchExact, int roleNumb) throws IOException, TransformerException {
        File sourceFile = exportCatalogXML(searchTerm, matchExact, roleNumb);

        File stylesheet = new File("bme_to_xhtml.xsl");

        TransformerFactory factory = TransformerFactory.newInstance();
        StreamSource stylesource = new StreamSource(stylesheet);

        Transformer transformer = null;
        try {
            transformer = factory.newTransformer(stylesource);
        } catch (TransformerConfigurationException e) {
            // TODO exception verarbeiten, 500er senden
            e.printStackTrace();
        }
        Source src = new StreamSource(sourceFile);
        // TODO does this work with windows?
        File file = new File("/tmp/output.xml");
        Result res = new StreamResult(file);
        transformer.transform(src, res);

        return file;

    }
}
