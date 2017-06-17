package de.htwg_konstanz.ebus.wholesaler.main;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
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
import java.util.List;

/**
 * @author mirko
 *
 *         Created on 01.06.17.
 * 
 *         Class containing static methods to perform import operations
 */
public class ImportUtil {

	/**
	 * Implements file upload functionalities using Apache Commons Fileupload
	 * and performs import operations
	 * 
	 * @param request:
	 *            the servlet request
	 * @param errorList:
	 *            the error list to transfer occurring errors
	 * @param infoList:
	 *            the info list to transfer state messages
	 * @return true if import was successful
	 */
	public static boolean importXmlFile(HttpServletRequest request, ArrayList<String> errorList,
			ArrayList<String> infoList) {
		int role = -1;
		if (ServletFileUpload.isMultipartContent(request)) {
			try {
				// Create a factory for disk-based file items
				DiskFileItemFactory factory = new DiskFileItemFactory();

				// Set factory constraints
				// factory.setSizeThreshold(yourMaxMemorySize); in bytes
				// factory.setRepository(yourTempDirectory); use system default

				// Create a new file upload handler
				ServletFileUpload upload = new ServletFileUpload(factory);

				// Set overall request size constraint
				// upload.setSizeMax(yourMaxRequestSize);

				// Parse the request
				List<FileItem> items = upload.parseRequest(request);

				// Process the uploaded items
				for (FileItem item : items) {

					if (item.isFormField()) {
						// processing a simple html form filed, e.g. for the
						// file name or the uploader
						String name = item.getFieldName();
						String value = item.getString();
						if (name.equals("role")) {
							role = Integer.parseInt(value);
						}
					} else {
						// processing the file
						String contentType = item.getContentType();
						long sizeInBytes = item.getSize();

						// no file selected
						if (sizeInBytes == 0) {
							errorList.add("No file selected for upload");
							return false;
						}

						// when its not a xml file
						if (!contentType.equals("text/xml")) {
							errorList.add("Wrong file type - just .xml files accepted");
							return false;
						}

						System.out.println("-- FILE IMPORT --");
						System.out.println("file size >" + sizeInBytes + "<");
						System.out.println("file content type >" + contentType + "<");

						InputStream uploadedStream = item.getInputStream();
						Document doc = null;

						// parsing stream to doc and check if its well formed
						try {
							doc = newDocumentFromInputStream(uploadedStream);
						} catch (SAXException se) {
							errorList.add("document is not well formed");
							return false;
						} catch (IOException | ParserConfigurationException e) {
							e.printStackTrace();
						}

						// check if the document is valid
						if (!validateXmlAgainstBmeCat(doc)) {
							errorList.add("document is not valid");
							return false;
						}

						IImportManager manager = new ImportManagerImpl();
						try {
							manager.storeAllArticles(doc, infoList);
						} catch (RuntimeException e) {
							System.err.println(e.getMessage());
							e.printStackTrace();
							errorList.add("Import canceled: " + e.getMessage());
							return false;
						}
						// sysout of the stream
						// https://stackoverflow.com/a/2345924
						// int size = 0;
						// byte[] buffer = new byte[1024];
						// while ((size = uploadedStream.read(buffer)) != -1)
						// System.out.write(buffer, 0, size);

						uploadedStream.close();
					}
				}
			} catch (FileUploadException | IOException e) {
				e.printStackTrace();
			}
		} else {
			errorList.add("file is not a multipart content");
			return false;
		}

		return true;
	}

	/**
	 * Creates an instance of Document (DOM) from a given InputStream
	 * 
	 * @param in:
	 *            the input stream to create the document from
	 * @return the Document instance
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	private static Document newDocumentFromInputStream(InputStream in)
			throws SAXException, ParserConfigurationException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		;
		DocumentBuilder builder = factory.newDocumentBuilder();
		// factory.setNamespaceAware(true);
		InputSource inSrc = new InputSource(in);
		// inSrc.setEncoding("UTF-8");

		return builder.parse(inSrc);
	}

	/**
	 * Validates a Document (DOM) against a locally stored XSD file (BMECAT)
	 * 
	 * @param document:
	 *            the document to validate
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

			System.out.println("Message = " + e.getMessage());
			System.out.println("Cause   = " + e.getCause());
			e.printStackTrace();

			return false;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return true;
	}
}
