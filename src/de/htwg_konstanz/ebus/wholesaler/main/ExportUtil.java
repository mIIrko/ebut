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
 *         Created on 2017-06-01
 *
 * Class containing static methods to perform export and tranformation operations
 */
public class ExportUtil {

    /**
     * Converts a given instance of Document (DOM) to an instance of File
     *
     * @param doc
     * @return
     * @throws IOException
     * @throws TransformerException
     */
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


    /**
     * Exports catalog in xml format as instance of File depending on searchTerm. If searchTerm is empty, all articles will be exported
     *
     * @param searchTerm: substring of short description property
     * @param matchExact: determine if exact match is mandatory
     * @param role: the current role
     * @return the file containing the catalog
     * @throws IOException
     * @throws TransformerException
     * @throws RuntimeException
     */
    public static File exportCatalogXML(String searchTerm, boolean matchExact, Role role) throws IOException, TransformerException, RuntimeException {

        ExportManagerImpl manager = new ExportManagerImpl(role);
        Document doc;
        if (searchTerm.equals("")) {
            doc = manager.retriveAllArticles();
        } else {
            doc = manager.retriveSelectiveArticles(searchTerm);
        }

        // validate the xml document againsts bme cat
        if (!ImportUtil.validateXmlAgainstBmeCat(doc)) {
            throw new RuntimeException(ExportError.EXPORTED_FILE_NOT_VALID.toString());
        } else {
            System.out.println("THE EXPORTED FILE IS VALID");
        }
        return convertDocToFile(doc);
    }

    /**
     * Exports catalog in xhtml format as instance of File depending on searchTerm. If searchTerm is empty, all articles will be exported
     *
     * @param searchTerm: substring of short description property
     * @param matchExact: determine if exact match is mandatory
     * @param roleNumb: number of the current role
     * @return the file containing the catalog
     * @throws IOException
     * @throws TransformerException
     * @throws RuntimeException
     */
    public static File exportCatalogXHTML(String searchTerm, boolean matchExact, Role role) throws IOException, TransformerException {

        File sourceFile = exportCatalogXML(searchTerm, matchExact, role);

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
