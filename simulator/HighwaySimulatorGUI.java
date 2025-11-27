package simulator;

import vehicles.Vehicle;
import interfaces.FuelConsumable;
import exceptions.InvalidOperationException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HighwaySimulatorGUI extends JFrame {
    private final JLabel counterLabel = new JLabel("Counter: 0 | Expected: 0");
    private final JTextArea helpArea = new JTextArea();
    private final JComboBox<SyncStrategy> strategyCombo = new JComboBox<>(SyncStrategy.values());
    private final JPanel vehiclesPanel = new JPanel();

    private HighwayCounter counter;
    private final List<VehicleRunner> runners = new ArrayList<>();
    private final List<Thread> threads = new ArrayList<>();
    private final List<JLabel> vehicleLabels = new ArrayList<>();
    private javax.swing.Timer uiTimer;
    
    private final List<Vehicle> fleet;

    public HighwaySimulatorGUI(List<Vehicle> fleet) {
        super("Fleet Highway Simulator");
        this.fleet = fleet;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Sync: "));
        strategyCombo.setSelectedItem(SyncStrategy.NONE);
        top.add(strategyCombo);

        JButton startBtn = new JButton("Start");
        JButton pauseBtn = new JButton("Pause");
        JButton resumeBtn = new JButton("Resume");
        JButton stopBtn = new JButton("Stop");
        JButton resetBtn = new JButton("Reset");
        top.add(startBtn);
        top.add(pauseBtn);
        top.add(resumeBtn);
        top.add(stopBtn);
        top.add(resetBtn);

        add(top, BorderLayout.NORTH);

        vehiclesPanel.setLayout(new BoxLayout(vehiclesPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(vehiclesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JPanel centerContainer = new JPanel(new BorderLayout());
        JPanel counterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        counterPanel.add(counterLabel);
        centerContainer.add(counterPanel, BorderLayout.NORTH);
        centerContainer.add(scrollPane, BorderLayout.CENTER);
        
        add(centerContainer, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        helpArea.setEditable(false);
        helpArea.setLineWrap(true);
        helpArea.setWrapStyleWord(true);
        helpArea.setVisible(false);
        bottom.add(helpArea, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        startBtn.addActionListener(e -> startSimulation());
        pauseBtn.addActionListener(e -> pauseSimulation());
        resumeBtn.addActionListener(e -> resumeSimulation());
        stopBtn.addActionListener(e -> stopSimulation());
        resetBtn.addActionListener(e -> resetSimulation());
        strategyCombo.addActionListener(e -> {
            SyncStrategy s = (SyncStrategy) strategyCombo.getSelectedItem();
            if (counter != null) counter.setStrategy(s);
            updateHelpText();
        });
        
        updateHelpText();
        initializeVehicleUI();
    }

    private void initializeVehicleUI() {
        vehiclesPanel.removeAll();
        vehicleLabels.clear();
        
        for (Vehicle v : fleet) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            
            JLabel label = new JLabel(getVehicleStatusText(v, 0, false));
            label.setPreferredSize(new Dimension(400, 25));
            vehicleLabels.add(label);
            row.add(label);
            
            if (v instanceof FuelConsumable) {
                JButton refuelBtn = new JButton("Refuel (+50)");
                refuelBtn.addActionListener(e -> {
                    for (VehicleRunner runner : runners) {
                         if (runner.getVehicle() == v) {
                             runner.refuel(50);
                             break;
                         }
                    }
                });
                row.add(refuelBtn);
            }
            
            vehiclesPanel.add(row);
        }
        vehiclesPanel.revalidate();
        vehiclesPanel.repaint();
    }
    
    private String getVehicleStatusText(Vehicle v, double fuelLevel, boolean outOfFuel) {
        String fuelInfo = "";
        if (v instanceof FuelConsumable) {
            fuelInfo = String.format(" | fuel %.1f%s", fuelLevel, outOfFuel ? " (out)" : "");
        } else {
            fuelInfo = " | (No Fuel Engine)";
        }
        return String.format("[%s] %s: %.0f km%s", 
                v.getClass().getSimpleName(), v.getModel(), v.getCurrentMileage(), fuelInfo);
    }

    private void updateHelpText() {
        helpArea.setVisible(false);
        helpArea.setText("");
        helpArea.revalidate();
    }

    private void startSimulation() {
        if (!threads.isEmpty() && threads.get(0).isAlive()) return;
        
        counter = new HighwayCounter();
        counter.setStrategy((SyncStrategy) strategyCombo.getSelectedItem());
        
        runners.clear();
        threads.clear();

        int incPerTick = strategyCombo.getSelectedItem() == SyncStrategy.NONE ? 250 : 1;
        long tick = 1;

        for (Vehicle v : fleet) {
            if (v instanceof FuelConsumable fc) {
                try {
                    fc.refuel(10000); 
                } catch (InvalidOperationException ignored) {}
            }

            VehicleRunner runner = new VehicleRunner(v, counter, tick, incPerTick);
            runners.add(runner);
            
            Thread t = new Thread(runner, v.getModel() + "Thread");
            threads.add(t);
            t.start();
        }

        if (uiTimer != null) uiTimer.stop();
        uiTimer = new javax.swing.Timer(250, e -> refreshLabels());
        uiTimer.start();
    }

    private void pauseSimulation() {
        runners.forEach(VehicleRunner::pause);
    }

    private void resumeSimulation() {
        runners.forEach(VehicleRunner::resume);
    }

    private void stopSimulation() {
        if (uiTimer != null) uiTimer.stop();
        runners.forEach(VehicleRunner::stop);
    }

    private void resetSimulation() {
        stopSimulation();
        
        for (Vehicle v : fleet) {
            v.resetMileage();
        }
        
        counter = new HighwayCounter();
        counter.setStrategy((SyncStrategy) strategyCombo.getSelectedItem());
        
        runners.clear();
        threads.clear();
        
        refreshLabels();
    }

    private void refreshLabels() {
        if (runners.isEmpty()) {
            for (int i = 0; i < fleet.size(); i++) {
                Vehicle v = fleet.get(i);
                JLabel label = vehicleLabels.get(i);
                double fuelLevel = (v instanceof FuelConsumable fc) ? fc.getFuelLevel() : 0;
                label.setText(getVehicleStatusText(v, fuelLevel, false));
            }
            counterLabel.setText("Counter: 0 | Total Fleet Mileage: 0");
            return;
        }
        
        double totalMileage = 0;
        
        for (int i = 0; i < runners.size(); i++) {
            VehicleRunner runner = runners.get(i);
            JLabel label = vehicleLabels.get(i);
            
            label.setText(getVehicleStatusText(runner.getVehicle(), runner.getFuelLevel(), runner.isOutOfFuel()));
            
            totalMileage += runner.getMileage();
        }

        int expected = (int) totalMileage;
        int actual = counter.get();
        
        String extra = (strategyCombo.getSelectedItem() == SyncStrategy.NONE && actual != expected) ? " | MISMATCH" : "";
        counterLabel.setText("Counter: " + actual + " | Total Fleet Mileage: " + (int)totalMileage + extra);
    }
    
    public static void main(String[] args) {
        List<Vehicle> testFleet = new ArrayList<>();
        try {
            testFleet.add(new vehicles.Car("C1", "TestCar", 100, 0, 4));
        } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new HighwaySimulatorGUI(testFleet).setVisible(true));
    }
}