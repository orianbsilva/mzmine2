package net.sf.mzmine.modules.peaklistmethods.identification.lipididentification.lipids;

import java.awt.Font;
import java.text.NumberFormat;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
import net.sf.mzmine.datamodel.IonizationType;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.peaklistmethods.identification.lipididentification.LipidSearchParameters;
import net.sf.mzmine.modules.peaklistmethods.identification.lipididentification.lipids.lipidmodifications.LipidModification;
import net.sf.mzmine.modules.peaklistmethods.identification.lipididentification.lipidutils.LipidIdentity;

public class LipidDatabaseTable extends JFrame {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private JPanel contentPane;
  public JScrollPane scrollPane;
  public JTable databaseTable;
  private LipidClasses[] selectedLipids;
  private int minChainLength = LipidSearchParameters.minChainLength.getValue();
  private int maxChainLength = LipidSearchParameters.maxChainLength.getValue();
  private int minDoubleBonds = LipidSearchParameters.minDoubleBonds.getValue();
  private int maxDoubleBonds = LipidSearchParameters.maxDoubleBonds.getValue();
  private IonizationType ionizationType = LipidSearchParameters.ionizationMethod.getValue();
  private LipidModification[] lipidModification = LipidSearchParameters.modification.getChoices();


  public LipidDatabaseTable(LipidClasses[] choices) {
    this.selectedLipids = choices;
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setBounds(100, 100, 800, 600);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(new MigLayout("", "[grow]", "[grow]"));

    scrollPane = new JScrollPane();
    contentPane.add(scrollPane, "cell 0 0,grow");

    databaseTable = new JTable();
    databaseTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
    databaseTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] {"ID", //
        "Lipid core class", //
        "Lipid Main class", //
        "Lipid class", //
        "Sum formula", //
        "Abbreviation", //
        "Ionization", //
        "Exact mass"}));
    databaseTable.setSurrendersFocusOnKeystroke(true);
    databaseTable.setFillsViewportHeight(true);
    databaseTable.setColumnSelectionAllowed(true);
    databaseTable.setCellSelectionEnabled(true);
    scrollPane.setViewportView(databaseTable);
    addDataToTable();
    validate();
  }

  private void addDataToTable() {

    DefaultTableModel model = (DefaultTableModel) databaseTable.getModel();
    NumberFormat numberFormat = MZmineCore.getConfiguration().getMZFormat();
    int id = 1;

    for (int i = 0; i < selectedLipids.length; i++) {
      int numberOfAcylChains = selectedLipids[i].getNumberOfAcylChains();
      int numberOfAlkylChains = selectedLipids[i].getNumberofAlkyChains();
      for (int chainLength = minChainLength; chainLength <= maxChainLength; chainLength++) {
        for (int chainDoubleBonds =
            minDoubleBonds; chainDoubleBonds <= maxDoubleBonds; chainDoubleBonds++) {

          // If we have non-zero fatty acid, which is shorter
          // than minimal length, skip this lipid
          if (((chainLength > 0) && (chainLength < minChainLength))) {
            continue;
          }

          // If we have more double bonds than carbons, it
          // doesn't make sense, so let's skip such lipids
          if (((chainDoubleBonds > 0) && (chainDoubleBonds > chainLength - 1))) {
            continue;
          }
          // Prepare a lipid instance
          LipidIdentity lipidChain = new LipidIdentity(selectedLipids[i], chainLength,
              chainDoubleBonds, numberOfAcylChains, numberOfAlkylChains);

          model.addRow(new Object[] {id, // id
              selectedLipids[i].getCoreClass().getName(), // core class
              selectedLipids[i].getMainClass().getName(), // main class
              selectedLipids[i].getName(), // lipid class
              lipidChain.getFormula(), // sum formula
              selectedLipids[i].getAbbr() + " (" + chainLength + ":" + chainDoubleBonds + ")", // abbr
              ionizationType, // ionization type
              numberFormat.format(lipidChain.getMass() + ionizationType.getAddedMass())});// exact
                                                                                          // mass
          id++;
          if (lipidModification.length > 0) {
            for (int j = 0; j < lipidModification.length; j++) {
              model.addRow(new Object[] {id, // id
                  selectedLipids[i].getCoreClass().getName(), // core class
                  selectedLipids[i].getMainClass().getName(), // main class
                  selectedLipids[i].getName() + " " + lipidModification[j].toString(), // lipid
                                                                                       // class
                  lipidChain.getFormula() + lipidModification[j].getLipidModificatio(), // sum
                                                                                        // formula
                  selectedLipids[i].getAbbr() + " (" + chainLength + ":" + chainDoubleBonds + ")"// abbr
                      + lipidModification[j].getLipidModificatio(),
                  ionizationType, // ionization type
                  numberFormat.format(lipidChain.getMass() + ionizationType.getAddedMass() // exact
                                                                                           // mass
                      + lipidModification[j].getModificationMass())});
              id++;
            }
          }
        }
      }
    }
  }

  public JTable getDatabaseTable() {
    return databaseTable;
  }

  public void setDatabaseTable(JTable databaseTable) {
    this.databaseTable = databaseTable;
  }
}
