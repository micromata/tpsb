package de.micromata.tpsb.doc.renderer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;

import de.micromata.genome.util.types.Pair;
import de.micromata.tpsb.doc.ParserConfig;
import de.micromata.tpsb.doc.ParserContext;
import de.micromata.tpsb.doc.parser.FileInfo;
import de.micromata.tpsb.doc.parser.MethodInfo;
import de.micromata.tpsb.doc.parser.ParameterInfo;
import de.micromata.tpsb.doc.parser.ParserResult;
import de.micromata.tpsb.doc.parser.TestStepInfo;

/**
 * Renderer für Test-Reports im Microsoft Office docx Format.
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public class MicrosoftWordRenderer extends AbstractResultRenderer {

	private final static Logger log = Logger.getLogger(MicrosoftWordRenderer.class);

	/** Das Word Template File was als Vorlage dient. */
	private String template;

	/** Das docx Dokument Modell. */
	private XWPFDocument document = null;

	/**
	 * Constructor
	 * 
	 * @param template
	 *            the word template.
	 */
	public MicrosoftWordRenderer(String template) {
		this.template = template;
	}

	@Override
  public void renderResult(ParserContext ctx, ParserConfig cfg) {
		ParserResult parserResult = ctx.getCurrentParserResult();
		try {
			document = new XWPFDocument(new FileInputStream(template));
		} catch (FileNotFoundException e1) {
			log.error("Microsoft Word docx Vorlage nicht gefunden.", e1);
		} catch (IOException e1) {
			log.error("Fehler beim Verarbeiten der Word Vorlage.", e1);
		}

		// Neue Seite
		addPageBreak();

		// Zusammenfassung
		addSummaryPage(parserResult);

		// Inhalt...
		addTestCases(parserResult);

		// Report speichern
		saveReport(cfg);
	}

	/**
	 * Fügt eine Zusammenfassung hinzu.
	 * 
	 * @param parserResult
	 */
	private void addSummaryPage(ParserResult parserResult) {
		addH1("Zusammenfassung");
		addPar("Erzeugt am",  new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()));
		addPar("Anzahl Tests", String.valueOf(parserResult.getMethodCount()));
		addPar("Erfolgreiche Tests", "TODO");
		addPar("Fehlgeschlage Tests", "TODO");
		addPageBreak();
	}

	/**
	 * Fügt die Testcases in die Word-Vorlage ein
	 * 
	 * @param parserResult
	 */
	private void addTestCases(ParserResult parserResult) {
		for (FileInfo fInfo : parserResult) {
			addH1(fInfo.getClassName());

			// Java-Doc Test-Klasse
			if (fInfo.getJavaDocInfo() != null && fInfo.getJavaDocInfo().getTitle() != null) {
				addPar("Beschreibung", fInfo.getJavaDocInfo().getTitle());
			}

			// Anzahl Tests
			addPar("Anzahl Tests", String.valueOf(fInfo.getMethodInfos().size()));

			// Autoren
			if (fInfo.getJavaDocInfo() != null && fInfo.getJavaDocInfo().getTagInfo("@author") != null
					&& fInfo.getJavaDocInfo().getTagInfo("@author").isEmpty() == false) {
				List<String> authors = new ArrayList<String>();
				for (Pair<String, String> author : fInfo.getJavaDocInfo().getTagInfo("@author")) {
					authors.add(author.getSecond());
				}
				addPar("Autoren Tests", authors.toArray(new String[authors.size()]));
			}

			// Einzelene Test-Methoden hinzufügen
			addTestMethods(fInfo);

			// neue Seite nach jeder Testklasse
			addPageBreak();
		}
	}

	/**
	 * Fügt einzele Tests aus einem Test-Case hinzu
	 * 
	 * @param fInfo
	 */
	private void addTestMethods(FileInfo fInfo) {
		for (MethodInfo mInfo : fInfo.getMethodInfos()) {
			addH2(mInfo.getMethodName());

			// Java-Doc Test-Methode
			if (mInfo.getJavaDocInfo() != null && mInfo.getJavaDocInfo().getTitle() != null) {
				addPar("Testbeschreibung", mInfo.getJavaDocInfo().getTitle());
			}

			// Autoren
			if (mInfo.getJavaDocInfo() != null && mInfo.getJavaDocInfo().getTagInfo("@author") != null
					&& mInfo.getJavaDocInfo().getTagInfo("@author").isEmpty() == false) {
				List<String> authors = new ArrayList<String>();
				for (Pair<String, String> author : mInfo.getJavaDocInfo().getTagInfo("@author")) {
					authors.add(author.getSecond());
				}
				addPar("Autoren Tests", authors.toArray(new String[authors.size()]));
			}
			addPar("Testablauf", mInfo.getTestSteps().size() == 0 ? "Keine Daten" : "");

			// Parameter in neue Zeilen hinzufügen
			XWPFTable testStepTable = document.createTable();
			setTableStyle(testStepTable, "TabelleProfessionell");
			XWPFTableRow headRow = testStepTable.getRow(0);
			headRow.getCell(0).setText("#");
			headRow.addNewTableCell().setText("Vorgehen");

			for (TestStepInfo tInfo : mInfo.getTestSteps()) {
				XWPFTableRow row = testStepTable.createRow();
				setColor("EDEDED", row.getCell(0).getCTTc()); // graue steps
				setColor("EDEDED", row.getCell(1).getCTTc());
				row.getCell(0).setText("#" + tInfo.getTestStep());
				if (tInfo.getTbJavaDocInfo() != null) {
					row.getCell(1).setText(tInfo.getTbJavaDocInfo().getTitle());
				} else {
					row.getCell(1).setText("Keine Daten");
				}

				// Aufrufparameter
				if (tInfo.getParameters() != null && tInfo.getParameters().isEmpty() == false) {
					for (ParameterInfo pInfo : tInfo.getParameters()) {
						XWPFTableRow pRow = testStepTable.createRow();
						XWPFParagraph p = pRow.getCell(1).getParagraphs().get(0);
						XWPFRun pRun = p.createRun();
						pRun.setText(pInfo.getJavaDoc() + ": " + pInfo.getParamValue());
						pRow.getCell(1).addParagraph(p);
					}
				}
			}

			// Falls vorhanden auch Screenshots in die Doku aufnehmen
			addScreenShotsIfExists(mInfo);
		}
	}

	/**
	 * Fügt Screenshots zum aktuellen Test in die Word-Vorlage ein, sofern einer
	 * oder mehrere vorhanden sind.
	 * 
	 * @param mInfo
	 *            die MethodInfos
	 */
	private void addScreenShotsIfExists(MethodInfo mInfo) {
		int counter = 1;
		do {
			String screenshotFileName = getScreenshotFileName(mInfo, counter);
			File screenshot = new File(screenshotFileName);
			if (screenshot.exists() == false) {
				return;
			}

			addPar("Screenshot " + counter, "");
			try {
				FileInputStream pic = new FileInputStream(screenshot);
				document.createParagraph().createRun()
						.addPicture(pic, Document.PICTURE_TYPE_PNG, "my pic", Units.toEMU(450), Units.toEMU(254));
			} catch (IOException e) {
				log.error("Fehler beim Rendern des Screenshots", e);
			} catch (InvalidFormatException e) {
				log.error("Fehler beim Rendern des Screenshots", e);
			}
			counter++;
		} while (true);
	}

	/**
	 * Liefert den Screenhost Dateinamen, für die aufgerufene Methode. Dieser
	 * Name folgt dem Muster
	 * 
	 * <pre>
	 * <Fullqualified Klassenname>_<Methodenname>_<Counter>.png (Punkte durch Unterstriche ersetzt)
	 * 
	 * z.B. de_micromata_dhl_amsel_abholportal_tas_TasOrderTest_testOrderTasSuccessWithUserAbr_1.png
	 * </pre>
	 * 
	 * @param mInfo
	 *            Methoden und KLasseninfo
	 * @param counter
	 *            der Zähler
	 * @return der Dateiname
	 */
	private String getScreenshotFileName(MethodInfo mInfo, int counter) {
		String screenshotSourceFolder = "target/.tpsb/";
		String screenshotFileName = mInfo.getClassName() + "." + mInfo.getMethodName();
		screenshotFileName = screenshotFileName.replace(".", "_");
		screenshotFileName += "_" + counter;
		screenshotFileName += ".png";
		screenshotFileName = screenshotSourceFolder + screenshotFileName;
		return screenshotFileName;
	}

	/**
	 * Speichert den Report im target Ordner.
	 * 
	 * @param cfg
	 *            die Report Konfiguration
	 */
	private void saveReport(ParserConfig cfg) {
		try {
			String fName = new File(new File(cfg.getOutputDir()), "Report").getAbsolutePath();
			if (fName.endsWith(getFileExtension()) == false) {
				fName = fName + "." + getFileExtension();
			}
			File file = new File(fName);
			document.write(new FileOutputStream(file));
			log.info("Word-Report geschrieben: " + file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			log.error("Fehler beinm Schreiben des Word-Reports", e);
		} catch (IOException e) {
			log.error("Fehler beinm Schreiben des Word-Reports", e);
		}
	}

	/**
	 * Fügt einen Seitemumbruch ein
	 */
	private void addPageBreak() {
		XWPFParagraph breakPar = document.createParagraph();
		XWPFRun run2 = breakPar.createRun();
		run2.addBreak(BreakType.PAGE);
	}

	/**
	 * Fügt eine Überschrift 1. Ebene ein
	 * 
	 * @param heading
	 *            der Titel
	 */
	private void addH1(String heading) {
		XWPFParagraph headPar = document.createParagraph();
		headPar.setStyle("berschrift1");
		XWPFRun run = headPar.createRun();
		run.setText(heading);
	}

	/**
	 * Fügt eine Überschrift 2. Ebene ein
	 * 
	 * @param heading
	 *            der Titel
	 */
	private void addH2(String heading) {
		XWPFParagraph headPar = document.createParagraph();
		headPar.setStyle("berschrift2");
		XWPFRun run = headPar.createRun();
		run.setText(heading);
	}

	/**
	 * Fügt einen neuen Paragraphen ein.
	 * 
	 * @param title
	 *            Titel des Abschnitts
	 * @param content
	 *            Inhalt (mehrere Zeilen möglich)
	 */
	private void addPar(String title, String... content) {
		XWPFParagraph par = document.createParagraph();
		XWPFRun titleRun = par.createRun();
		titleRun.setBold(true);
		titleRun.addCarriageReturn();
		titleRun.setText(title);
		titleRun.addCarriageReturn();
		for (String line : content) {
			XWPFRun contentRun = par.createRun();
			contentRun.setText(line);
		}
	}

	/**
	 * Setzt eine Formatvorlage für eine Tabelle. Diese Formatvorlage muss in
	 * der Word Vorlage definiert sein.
	 * 
	 * @param table
	 *            die Tabelle
	 * @param formatTemplate
	 *            die Format-Vorlage
	 */
	private void setTableStyle(XWPFTable table, String formatTemplate) {
		CTString tblStyle = table.getCTTbl().getTblPr().getTblStyle() == null ? table.getCTTbl().getTblPr()
				.addNewTblStyle() : table.getCTTbl().getTblPr().getTblStyle();
		tblStyle.setVal(formatTemplate);
	}

	/**
	 * Set cell color. This sets some associated values; for finer control you
	 * may want to access these elements individually.
	 * 
	 * @param rgbStr
	 *            - the desired cell color, in the hex form "RRGGBB".
	 */
	private void setColor(String rgbStr, CTTc ctTc) {
		CTTcPr tcpr = ctTc.isSetTcPr() ? ctTc.getTcPr() : ctTc.addNewTcPr();
		CTShd ctshd = tcpr.isSetShd() ? tcpr.getShd() : tcpr.addNewShd();
		ctshd.setColor("auto");
		ctshd.setVal(STShd.CLEAR);
		ctshd.setFill(rgbStr);
	}

	/**
	 * Liefert die Datei-Endung der Report-Datei
	 */
	@Override
  public String getFileExtension() {
		return "docx";
	}
}