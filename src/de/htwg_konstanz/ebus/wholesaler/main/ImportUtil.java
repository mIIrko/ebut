package de.htwg_konstanz.ebus.wholesaler.main;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author mirko
 *         <p>
 *         Created on 01.06.17.
 *         <p>
 *         Class containing static methods to perform import operations
 */
public class ImportUtil {

    /**
     * Implements file upload functionalities using Apache Commons Fileupload
     * and performs import operations
     *
     * @param role:   the role from the user
     * @param errorList: the error list to transfer occurring errors
     * @param infoList:  the info list to transfer state messages
     * @return true if import was successful
     */
    public static boolean importXmlFile(Role role,
                                        Document doc,
                                        ArrayList<String> errorList,
                                        ArrayList<String> infoList) {

        // check if the document is valid
        if (!validateXmlAgainstBmeCat(doc)) {
            errorList.add("document is not valid");
            return false;
        }

        IImportManager manager = new ImportManagerImpl();
        try {
            manager.storeAllArticles(doc, infoList);
        } catch (RuntimeException e) {
            errorList.add("Import canceled: " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Creates an instance of Document (DOM) from a given InputStream
     *
     * @param in: the input stream to create the document from
     * @return the Document instance
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public static Document newDocumentFromInputStream(InputStream in)
        throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = factory.newDocumentBuilder();
        // factory.setNamespaceAware(true);
        InputSource inSrc = new InputSource(in);
        // inSrc.setEncoding("UTF-8");

        Document doc = builder.parse(inSrc);

        try {
            in.close();
        } catch (IOException e) {
           e.printStackTrace();
        }

        return doc;
    }

    /**
     * Validates a Document (DOM) against a locally stored XSD file (BMECAT)
     *
     * @param document: the document to validate
     * @return true if the document is valid
     */
    public static boolean validateXmlAgainstBmeCat(Document document) {

        // create a SchemaFactory capable of understanding W3C XML Schema (WXS)
        // schemas
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // load a WXS schema, represented by a Schema instance
        Source schemaFile = new StreamSource(new File("bmecat_new_catalog_1_2_simple_without_NS.xsd"));
        Schema schema = null;
        try {
            schema = factory.newSchema(schemaFile);
        } catch (SAXException se) {
            System.err.println("DAS XML SCHEMA IST NICHT VALIDE!");
            se.printStackTrace();
        }

        // create a Validator instance, which can be used to validate an
        // instance document
        Validator validator = schema.newValidator();

        // validate the DOM tree
        try {
            validator.validate(new DOMSource(document));
        } catch (SAXException e) {
            // instance document is invalid!

            System.out.println("VALIDATING : Message = " + e.getMessage());
            System.out.println("VALIDATING : Cause   = " + e.getCause());
            return false;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return true;
    }
}
