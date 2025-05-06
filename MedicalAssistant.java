import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.GridLayout;
import java.awt.List; // Explicitly for AWT List component
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Custom exceptions
class UnrecognizedSymptomException extends Exception {
    public UnrecognizedSymptomException(String message) {
        super(message);
    }
}

class UnrecognizedDiseaseException extends Exception {
    public UnrecognizedDiseaseException(String message) {
        super(message);
    }
}

class MedicalDataBase {
    private Map<String, Set<String>> symptomToDiseases;
    private Map<String, Set<String>> diseaseToSymptoms;
    private java.util.List<String> patientSymptoms; // Explicitly use java.util.List
    private Set<String> validSymptoms;
    private Set<String> validDiseases;
    private String patientId;
    private String patientName;

    public MedicalDataBase(String patientId, String patientName) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.symptomToDiseases = new HashMap<>();
        this.diseaseToSymptoms = new HashMap<>();
        this.patientSymptoms = new ArrayList<>();
        this.validSymptoms = new HashSet<>();
        this.validDiseases = new HashSet<>();
    }

    public String initializeFiles() {
        try {
            // Create symptoms.txt with 30 symptoms if it doesn't exist
            File symptomsFile = new File("symptoms.txt");
            if (!symptomsFile.exists()) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(symptomsFile))) {
                    String[] symptoms = {
                        "fever", "cough", "fatigue", "headache", "sore throat",
                        "nausea", "vomiting", "diarrhea", "shortness of breath", "chest pain",
                        "joint pain", "muscle pain", "rash", "chills", "dizziness",
                        "abdominal pain", "loss of appetite", "sweating", "swollen lymph nodes", "weight loss",
                        "sneezing", "runny nose", "back pain", "itchy eyes", "ear pain",
                        "difficulty swallowing", "dry mouth", "numbness", "blurred vision", "constipation"
                    };
                    for (String symptom : symptoms) {
                        writer.write(symptom + "\n");
                    }
                }
            }

            // Create disease.txt with 30 diseases if it doesn't exist
            File diseaseFile = new File("disease.txt");
            if (!diseaseFile.exists()) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(diseaseFile))) {
                    String[] diseases = {
                        "flu", "common cold", "migraine", "pneumonia", "bronchitis",
                        "gastroenteritis", "appendicitis", "arthritis", "dengue fever", "malaria",
                        "typhoid fever", "strep throat", "sinusitis", "tonsillitis", "urinary tract infection",
                        "mononucleosis", "hepatitis", "meningitis", "lyme disease", "chronic fatigue syndrome",
                        "asthma", "conjunctivitis", "ear infection", "gastritis", "hypertension",
                        "diabetes", "anemia", "eczema", "sprain", "kidney stones"
                    };
                    for (String disease : diseases) {
                        writer.write(disease + "\n");
                    }
                }
            }

            // Create medicalDatabase.csv with default mappings if it doesn't exist
            File csvFile = new File("medicalDatabase.csv");
            if (!csvFile.exists()) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
                    writer.write("patient id|patient name|symptoms list|possible disease\n");
                    String[] mappings = {
                        "P000|Setup|fever,cough,fatigue|flu",
                        "P000|Setup|fever,cough,sore throat,sneezing,runny nose|common cold",
                        "P000|Setup|headache,dizziness,blurred vision|migraine",
                        "P000|Setup|cough,shortness of breath,chest pain|pneumonia",
                        "P000|Setup|cough,shortness of breath|bronchitis",
                        "P000|Setup|nausea,vomiting,diarrhea|gastroenteritis",
                        "P000|Setup|abdominal pain,fever|appendicitis",
                        "P000|Setup|joint pain,muscle pain|arthritis",
                        "P000|Setup|fever,rash,chills|dengue fever",
                        "P000|Setup|fever,chills,sweating|malaria",
                        "P000|Setup|fever,abdominal pain,weight loss|typhoid fever",
                        "P000|Setup|sore throat,swollen lymph nodes|strep throat",
                        "P000|Setup|headache,fever,runny nose|sinusitis",
                        "P000|Setup|sore throat,fever,difficulty swallowing|tonsillitis",
                        "P000|Setup|abdominal pain,diarrhea,constipation|urinary tract infection",
                        "P000|Setup|fatigue,swollen lymph nodes|mononucleosis",
                        "P000|Setup|nausea,vomiting,abdominal pain|hepatitis",
                        "P000|Setup|fever,headache,dizziness|meningitis",
                        "P000|Setup|rash,joint pain|lyme disease",
                        "P000|Setup|fatigue,weight loss|chronic fatigue syndrome",
                        "P000|Setup|shortness of breath,cough|asthma",
                        "P000|Setup|itchy eyes,runny nose|conjunctivitis",
                        "P000|Setup|ear pain,fever|ear infection",
                        "P000|Setup|abdominal pain,nausea|gastritis",
                        "P000|Setup|dizziness,chest pain|hypertension",
                        "P000|Setup|fatigue,weight loss|diabetes",
                        "P000|Setup|fatigue,numbness|anemia",
                        "P000|Setup|rash,itching|eczema",
                        "P000|Setup|joint pain,back pain|sprain",
                        "P000|Setup|abdominal pain,difficulty urinating|kidney stones"
                    };
                    for (String mapping : mappings) {
                        writer.write(mapping + "\n");
                    }
                }
            }
            return null;
        } catch (IOException e) {
            return "Error initializing files: " + e.getMessage();
        }
    }

    public String loadSymptoms() {
        try {
            File symptomsFile = new File("symptoms.txt");
            if (!symptomsFile.exists()) {
                return "symptoms.txt not found. Please ensure the file exists.";
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(symptomsFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String symptom = line.trim().toLowerCase();
                    if (!symptom.isEmpty()) {
                        validSymptoms.add(symptom);
                    }
                }
                if (validSymptoms.isEmpty()) {
                    return "Warning: symptoms.txt is empty. Please add symptoms to the file.";
                }
            }
            return null;
        } catch (IOException e) {
            return "Error reading symptoms.txt: " + e.getMessage();
        }
    }

    public String loadDiseases() {
        try {
            File diseaseFile = new File("disease.txt");
            if (!diseaseFile.exists()) {
                return "disease.txt not found. Please ensure the file exists.";
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(diseaseFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String disease = line.trim().toLowerCase();
                    if (!disease.isEmpty()) {
                        validDiseases.add(disease);
                    }
                }
                if (validDiseases.isEmpty()) {
                    return "Warning: disease.txt is empty. Please add diseases to the file.";
                }
            }
            return null;
        } catch (IOException e) {
            return "Error reading disease.txt: " + e.getMessage();
        }
    }

    public String loadDatabase() {
        try {
            File file = new File("medicalDatabase.csv");
            if (!file.exists()) {
                return null;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                reader.readLine(); // Skip header
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 4) {
                        String symptoms = parts[2].trim().toLowerCase();
                        String disease = parts[3].trim().toLowerCase();
                        for (String symptom : symptoms.split(",")) {
                            symptom = symptom.trim();
                            if (!symptom.isEmpty()) {
                                symptomToDiseases.computeIfAbsent(symptom, _ -> new HashSet<>()).add(disease);
                                diseaseToSymptoms.computeIfAbsent(disease, _ -> new HashSet<>()).add(symptom);
                            }
                        }
                    }
                }
            }
            return null;
        } catch (IOException e) {
            return "Error reading medicalDatabase.csv: " + e.getMessage();
        }
    }

    public void addSymptom(String symptom) throws UnrecognizedSymptomException {
        String normalizedSymptom = symptom.trim().toLowerCase();
        if (!validSymptoms.contains(normalizedSymptom)) {
            throw new UnrecognizedSymptomException("Symptom '" + symptom + "' not recognized");
        }
        if (!patientSymptoms.contains(normalizedSymptom)) {
            patientSymptoms.add(normalizedSymptom);
        }
    }

    public String[] diagnosis() throws UnrecognizedDiseaseException {
        Set<String> possibleDiseases = new HashSet<>();
        boolean firstSymptom = true;

        for (String symptom : patientSymptoms) {
            Set<String> diseases = symptomToDiseases.getOrDefault(symptom, new HashSet<>());
            for (String disease : diseases) {
                if (!validDiseases.contains(disease)) {
                    throw new UnrecognizedDiseaseException("Disease '" + disease + "' not recognized");
                }
            }
            if (firstSymptom) {
                possibleDiseases.addAll(diseases);
                firstSymptom = false;
            } else {
                possibleDiseases.retainAll(diseases);
            }
        }

        if (patientSymptoms.isEmpty()) {
            return new String[0];
        }

        return possibleDiseases.toArray(new String[0]);
    }

    public String saveToCSV() {
        try {
            validateCSVFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("medicalDatabase.csv", true))) {
                String symptoms = String.join(",", patientSymptoms);
                String diseases = String.join(",", diagnosis());
                String record = String.format("%s|%s|%s|%s\n", patientId, patientName, symptoms, diseases);
                writer.write(record);
            }
            return null;
        } catch (IOException | UnrecognizedDiseaseException e) {
            return "Error writing to medicalDatabase.csv: " + e.getMessage();
        }
    }

    private void validateCSVFile() throws IOException {
        File file = new File("medicalDatabase.csv");
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("patient id|patient name|symptoms list|possible disease\n");
            }
        }
        if (!file.getName().endsWith(".csv")) {
            throw new IOException("Invalid file type: Must be a .csv file");
        }
    }

    public Set<String> getValidSymptoms() {
        return validSymptoms;
    }

    public Set<String> getValidDiseases() {
        return validDiseases;
    }

    public void clearSymptoms() {
        patientSymptoms.clear();
    }
}

public class MedicalAssistant extends Frame implements ActionListener, WindowListener {
    private TextField patientIdField, patientNameField;
    private List symptomList; // Refers to java.awt.List
    private TextArea resultArea;
    private Button diagnoseButton;
    private MedicalDataBase db;

    public MedicalAssistant() {
        // Initialize MedicalDataBase with default values
        db = new MedicalDataBase("P000", "Unknown");

        // Perform initialization and load data
        String initError = db.initializeFiles();
        if (initError != null) {
            showErrorAndExit(initError);
            return;
        }

        String symptomsError = db.loadSymptoms();
        if (symptomsError != null) {
            showErrorAndExit(symptomsError);
            return;
        }

        String diseasesError = db.loadDiseases();
        if (diseasesError != null) {
            showErrorAndExit(diseasesError);
            return;
        }

        String databaseError = db.loadDatabase();
        if (databaseError != null) {
            showErrorAndExit(databaseError);
            return;
        }

        // Set up the Frame
        setTitle("Medical Assistant");
        setSize(600, 400);
        setLayout(new BorderLayout());
        setBackground(java.awt.Color.LIGHT_GRAY);

        // North Panel: Patient Information
        Panel northPanel = new Panel(new GridLayout(2, 2, 5, 5));
        northPanel.setBackground(java.awt.Color.LIGHT_GRAY);
        northPanel.add(new Label("Patient ID:"));
        patientIdField = new TextField(20);
        northPanel.add(patientIdField);
        northPanel.add(new Label("Patient Name:"));
        patientNameField = new TextField(20);
        northPanel.add(patientNameField);
        add(northPanel, BorderLayout.NORTH);

        // Center Panel: Symptoms Selection
        Panel centerPanel = new Panel(new BorderLayout());
        centerPanel.add(new Label("Select Symptoms (hold Ctrl for multiple):"), BorderLayout.NORTH);
        symptomList = new List(10, true); // java.awt.List
        for (String symptom : db.getValidSymptoms()) {
            symptomList.add(symptom.toLowerCase());
        }
        centerPanel.add(symptomList, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // South Panel: Button and Results
        Panel southPanel = new Panel(new BorderLayout());
        diagnoseButton = new Button("Diagnose and Save");
        diagnoseButton.addActionListener(this);
        southPanel.add(diagnoseButton, BorderLayout.NORTH);
        resultArea = new TextArea("Diagnosis results will appear here...", 5, 50);
        resultArea.setEditable(false);
        southPanel.add(resultArea, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Add WindowListener to handle window closing
        addWindowListener(this);

        // Check if symptom or disease data is missing
        if (db.getValidSymptoms().isEmpty() || db.getValidDiseases().isEmpty()) {
            resultArea.setText("Error: Symptom or disease data is missing. Check symptoms.txt and disease.txt.");
            diagnoseButton.setEnabled(false);
        }

        setVisible(true);
    }

    private void showErrorAndExit(String message) {
        Frame errorFrame = new Frame("Error");
        errorFrame.setLayout(new BorderLayout());
        errorFrame.setSize(400, 150);
        errorFrame.add(new Label(message), BorderLayout.CENTER);
        Button okButton = new Button("OK");
        okButton.addActionListener(_ -> {
            errorFrame.dispose();
            dispose();
            System.exit(1);
        });
        errorFrame.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent we) {
                errorFrame.dispose();
                dispose();
                System.exit(1);
            }
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        errorFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == diagnoseButton) {
            String patientId = patientIdField.getText().trim();
            String patientName = patientNameField.getText().trim();

            // Validate patient ID and name
            if (patientId.isEmpty() || patientName.isEmpty()) {
                resultArea.setText("Error: Please enter both Patient ID and Patient Name.");
                return;
            }

            // Reinitialize MedicalDataBase with user-provided ID and name
            db = new MedicalDataBase(patientId, patientName);
            String initError = db.initializeFiles();
            if (initError != null) {
                resultArea.setText(initError);
                return;
            }
            String symptomsError = db.loadSymptoms();
            if (symptomsError != null) {
                resultArea.setText(symptomsError);
                return;
            }
            String diseasesError = db.loadDiseases();
            if (diseasesError != null) {
                resultArea.setText(diseasesError);
                return;
            }
            String databaseError = db.loadDatabase();
            if (databaseError != null) {
                resultArea.setText(databaseError);
                return;
            }

            // Clear previous symptoms
            db.clearSymptoms();

            // Add selected symptoms
            String[] selectedSymptoms = symptomList.getSelectedItems();
            if (selectedSymptoms.length == 0) {
                resultArea.setText("Error: Please select at least one symptom.");
                return;
            }

            StringBuilder errorMessages = new StringBuilder();
            for (String symptom : selectedSymptoms) {
                try {
                    db.addSymptom(symptom);
                } catch (UnrecognizedSymptomException ex) {
                    errorMessages.append(ex.getMessage()).append("\n");
                }
            }

            if (errorMessages.length() > 0) {
                resultArea.setText(errorMessages.toString());
                return;
            }

            // Perform diagnosis and save
            try {
                String[] diagnoses = db.diagnosis();
                if (diagnoses.length == 0) {
                    resultArea.setText("No diagnoses found for the selected symptoms.");
                } else {
                    resultArea.setText("Possible diagnoses: " + Arrays.toString(diagnoses));
                }
                String saveError = db.saveToCSV();
                if (saveError != null) {
                    resultArea.append("\n" + saveError);
                }
            } catch (UnrecognizedDiseaseException ex) {
                resultArea.setText("Error: " + ex.getMessage());
            }
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        dispose();
        System.exit(0);
    }

    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new MedicalAssistant());
    }
}