package de.htwg_konstanz.ebus.wholesaler.main;

import java.io.*;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

/**
 * @author mirko bay
 *         2017-06-01
 *         <p>
 *         Class for retrieving and transforming the data;
 *         throws all exceptions to the actions
 */
public class ExportUtil {

    public static File convertDocToFile(Document doc) throws IOException, TransformerException {
        DOMSource source = new DOMSource(doc);
        // todo: filename and path in constants class
        File file = new File("output.xml");
        FileWriter writer = new FileWriter(file);
        StreamResult result = new StreamResult(writer);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(source, result);

        writer.flush();
        writer.close();

        return file;
    }

    public static File exportCatalogXML(String searchTerm, boolean matchExact, int roleNumb) throws IOException, TransformerException, RuntimeException {

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

        // output the file for debugging
        try (FileInputStream inputStream = new FileInputStream(sourceFile)) {
            String everything = IOUtils.toString(inputStream);
            // do something with everything string
            System.out.println(everything);
        }

        File styleSheet = new File("bme_to_xhtml.xsl");

        TransformerFactory factory = TransformerFactory.newInstance();
        StreamSource styleSource = new StreamSource(styleSheet);

        Transformer transformer = null;
        // set the style sheet to the transformer
        try {
            transformer = factory.newTransformer(styleSource);
        } catch (TransformerConfigurationException e) {
            // TODO exception verarbeiten, 500er senden
            e.printStackTrace();
        }

        StreamSource in = new StreamSource(sourceFile);

        File outputFile = new File("out.xhtml");
        StreamResult out = new StreamResult(outputFile);

        try {
            transformer.transform(in, out);
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return outputFile;

    }
}
