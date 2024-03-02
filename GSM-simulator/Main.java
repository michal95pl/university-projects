import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

interface ScrollPanelListener {
    void addEvent(EventObject e);
    void deleteEvent(EventObject e);
}

interface SMSBus {
    void receive(SMS sms);
    int getNumberWaitingSMS();
    Long getDestinationId();
}

interface UpdateBSC {
    void setNewSMSBus(List<SMSBus> smsBus);
    void destroy();
}

interface UpdateVRDGraphicListener {
    void updateGraphic(EventObject e,  Integer smsCount);
}

interface VBDLogicalInterface {
    Long getNumber();
    void setFreq(int val);
    void delete();
    void stopWorking();
    void resumeWorking();
}

interface VRDLogicalInterface {
    Long getNumber();
    void destroy();
    void setDeleteMessageCount(boolean data);
}

interface BTSLogicaInterface {
    int getNumber();
}

interface BSCLogicalInterface {
    int getNumber();
    void delete();
}

class StationEvent
        extends EventObject {

    public StationEvent(Object source) {
        super(source);
    }
}

class DeviceEvent
        extends EventObject {
    public DeviceEvent(Object source) {
        super(source);
    }
}

interface StationUpdate {
    void update(StationEvent e, int numberWaiting, int numberProcessed);
}

class ScrollPanelEvent
        extends EventObject {
    public ScrollPanelEvent(Object source) {
        super(source);
    }
}


abstract class Station<listener>
        extends JPanel {

    private static Long lastNumber = 111_111_110L;
    private Long number;
    static Long createUniqNumber() {
        return ++Station.lastNumber;
    }

    protected Integer smsProcessedNumber = 0;

    protected JLabel idLabel;
    protected JLabel smsPrecessed;
    protected JLabel smsWaiting;

    protected LinkedBlockingQueue<SMS> queue;
    protected ArrayList<SMSBus> smsBus;

    protected SMSBus getLeastLoaded() {

        if (smsBus != null && smsBus.size() > 0) {
            SMSBus temp = smsBus.get(0);

            for (SMSBus i : smsBus) {
                if (i.getNumberWaitingSMS() < temp.getNumberWaitingSMS())
                    temp = i;
            }

            return temp;
        }

        return null;
    }

    Station(ArrayList<SMSBus> smsBus) {
        this.setLayout(new GridLayout(3,1));
        this.number = createUniqNumber();
        this.queue = new LinkedBlockingQueue<>();
        this.smsBus = smsBus;

        idLabel = new JLabel(this.number.toString());
        smsPrecessed = new JLabel("Processed: " + this.smsProcessedNumber.toString());
        smsWaiting = new JLabel("Waiting: " + this.queue.size());

        this.add(idLabel);
        this.add(smsPrecessed);
        this.add(smsWaiting);
    }

    public void setNewSMSBus(ArrayList<SMSBus> smsBus) {
        this.smsBus = smsBus;
    }

    protected void refresh() {
        this.smsWaiting.setText("Waiting: " + this.queue.size());
        this.validate();
        this.repaint();
    }

    // listeners
    protected final ArrayList<listener> listenerScrollArrayList = new ArrayList<>();

    public void addScrollListener(listener l) {
        this.listenerScrollArrayList.add(l);
    }

    public void removeScrollListener(listener l) {
        this.listenerScrollArrayList.remove(l);
    }
}


class SMS {

    ArrayList<Integer> data;

    private ArrayList<Integer> getDigits(long number) {
        ArrayList<Integer> digits = new ArrayList<>();
        for (long i = number; i > 0; i /= 10) {
            digits.add(0, (int)(i%10) );
        }
        return digits;
    }

    private void addressField(ArrayList<Integer> data, long address) {
        ArrayList<Integer> digits = getDigits(address);

        data.add( (int)Math.ceil(digits.size()/2.) + 1); // length SMSC
        data.add(0x91); // default type of adress (international)

        // address value (sender)
        for(int i=0; i < digits.size(); ) {
            int temp = 0;
            temp |= digits.get(i++).byteValue();

            if (i+1 > digits.size())
                temp |= (0xF << 4);
            else
                temp |= (digits.get(i++).byteValue() << 4);
            data.add(temp);
        }
    }

    SMS(long senderNumber, long recipientNumber, String msg) {

        data = new ArrayList<>();

        // SMSC
        addressField(data, senderNumber);

        // tpdu
        data.add(1); // default, 1 octet
        data.add(1); // tp-mr, id

        addressField(data, recipientNumber);// tp-da, address destination

        data.add(0x20); // tp-pid
        data.add(0x4); // 8-bit alphabet, max 140 char

        data.add(msg.length()); // length user-data
        for (int i=0; i < msg.length() && i < 140; i++)
            data.add((int) msg.charAt(i));
    }

    public String getMessage() {

        StringBuilder temp = new StringBuilder();

        int indexStartMsg = data.get(0) + 1;
        indexStartMsg += 2;
        indexStartMsg += data.get(indexStartMsg) + 1;
        indexStartMsg += 2;

        for (int i=0; i < data.get(indexStartMsg); i++) {
            temp.append((char) (int) data.get(indexStartMsg + 1 + i));
        }

        return temp.toString();
    }

    public int getRNumber() {
        int indexStartRNumber = data.get(0) + 1;
        indexStartRNumber += 2;

        int temp = 0;
        int mult = 1;

        for (int i=data.get(indexStartRNumber)-2; i >= 0; i--) {

            int t = ((data.get(indexStartRNumber + i + 2) & 0xF0) >> 4);
            if (t != 0xF) {
                temp += t * mult;
                mult *= 10;
            }

            temp += (data.get(indexStartRNumber + i + 2) & 0xF) * mult;
            mult *= 10;

        }

        return temp;
    }


}

class DataContainer {

    private String msg;
    private Integer cnt=0;
    private Short messageLength;

    DataContainer(String msg) {
        this.msg = msg;
        messageLength = (short)msg.length();
    }

    void incrementCount() {
        this.cnt++;
    }

    ArrayList<Byte> getBinRepresentation(Long id) {
        ArrayList<Byte> data = new ArrayList<>();

        if (id < 0)
            throw new RuntimeException("id < 0");

        //id (8 bytes)
        data.add( (byte) ((id >> 56) & 0xFF) );
        data.add( (byte) ((id >> 48) & 0xFF)  );
        data.add( (byte) ((id >> 40) & 0xFF)  );
        data.add( (byte) ((id >> 32) & 0xFF)  );
        data.add( (byte) ((id >> 24) & 0xFF) );
        data.add( (byte) ((id >> 16) & 0xFF)  );
        data.add( (byte) ((id >> 8) & 0xFF)  );
        data.add( (byte) ((id >> 0) & 0xFF)  );

        // cnt (4 bytes)
        data.add( (byte) ((cnt >> 24) & 0xFF) );
        data.add( (byte) ((cnt >> 16) & 0xFF)  );
        data.add( (byte) ((cnt >> 8) & 0xFF)  );
        data.add( (byte) ((cnt >> 0) & 0xFF)  );


        // length (2 bytes)
        data.add( (byte) ((messageLength >> 8) & 0xFF)  );
        data.add( (byte) ((messageLength >> 0) & 0xFF)  );

        for (int i=0; i < msg.length(); i++)
            data.add((byte)msg.charAt(i));

        return data;
    }

}

class LogicDevice {

    LinkedBlockingQueue<SMS> queue = new LinkedBlockingQueue<>();

    private static Long lastNumber = 111_111_110L;
    private final Long number;
    private static Long createUniqNumber() {
        return lastNumber++;
    }

    LogicDevice() {
        number = createUniqNumber();
    }

    public Long getNumber() {
        return this.number;
    }
}

class GraphicDevice
        extends JPanel {

    protected JButton button;
    JTextField textField;

    GraphicDevice(Long number) {
        this.setPreferredSize(new Dimension(100,200));
        this.setLayout(new FlowLayout(FlowLayout.CENTER));

        this.textField = new JTextField(number.toString());
        this.textField.setEditable(false);
        this.button = new JButton("delete");
    }

    // listeners
    protected final ArrayList<ScrollPanelListener> scrollListenerArrayList = new ArrayList<>();

    public void addScrollListener(ScrollPanelListener l) {
        this.scrollListenerArrayList.add(l);
    }

    public void removeScrollListener(ScrollPanelListener l) {
        this.scrollListenerArrayList.remove(l);
    }

}


class LogicStation {
    private static int lastNumber = 0;
    protected final int number;
    static int createUniqNumber() {
        return ++lastNumber;
    }
    protected Integer smsProcessedNumber = 0;
    protected LinkedBlockingQueue<SMS> queue;
    protected List<SMSBus> smsBus; // to send

    protected SMSBus getLeastLoaded(List<SMSBus> smsBus) {

        if (smsBus != null && smsBus.size() > 0) {
            SMSBus temp = smsBus.get(0);

            synchronized (smsBus) {
                for (SMSBus i : smsBus) {
                    if (i.getNumberWaitingSMS() < temp.getNumberWaitingSMS())
                        temp = i;
                }
            }


            return temp;
        }

        return null;
    }

    LogicStation(final List<SMSBus> smsBus) {
        this.number = createUniqNumber();
        this.smsBus = smsBus;
        this.queue = new LinkedBlockingQueue<>();
    }

    // scroll listeners
    protected final ArrayList<ScrollPanelListener> listenerScrollArrayList = new ArrayList<>();

    public void addScrollListener(ScrollPanelListener l) {
        this.listenerScrollArrayList.add(l);
    }

    public void removeScrollListener(ScrollPanelListener l) {
        this.listenerScrollArrayList.remove(l);
    }

    // update listeners
    protected ArrayList<StationUpdate> stationUpdateListeners = new ArrayList<>();

    // listener
    public void addUpdateListener(StationUpdate e) {
        stationUpdateListeners.add(e);
    }

    public void removeUpdateListener(StationUpdate e) {
        stationUpdateListeners.remove(e);
    }

    protected void fireUpdateGraphic() {
        for (StationUpdate i : this.stationUpdateListeners)
            i.update(
                    new StationEvent(this), this.queue.size(), this.smsProcessedNumber
            );
    }
}

class GraphicStation
        extends JPanel
        implements StationUpdate {
    protected JLabel idLabel;
    protected JLabel smsPrecessed;
    protected JLabel smsWaiting;

    GraphicStation(final Integer number) {
        this.setLayout(new GridLayout(3,1));

        idLabel = new JLabel("id: " + number.toString());
        smsPrecessed = new JLabel("Processed: " + 0);
        smsWaiting = new JLabel("Waiting: " + 0);

        this.add(idLabel);
        this.add(smsPrecessed);
        this.add(smsWaiting);
    }

    @Override
    public void update(StationEvent e, int numberWaiting, int numberProcessed) {
        refresh(numberWaiting, numberProcessed);
    }

    protected void refresh(int waiting, int processed) {
        this.smsWaiting.setText("Waiting: " + waiting);
        this.smsPrecessed.setText("Processed: " + processed);
        this.validate();
        this.repaint();
    }
}



class BTSLogic
        extends LogicStation
        implements Runnable, SMSBus, BTSLogicaInterface {

    BTSLogic(final List<SMSBus> smsBus) {
        super(smsBus);
        new Thread(this).start();
    }


    @Override
    public void run() {
        while(true) {
            // send to first bsc
            SMSBus bus = getLeastLoaded(this.smsBus);
            if (bus != null && this.queue.size() > 0) {
                this.smsProcessedNumber++;
                bus.receive(this.queue.remove());
            }

            super.fireUpdateGraphic();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public synchronized void receive(SMS sms) {
        this.queue.add(sms);
        super.fireUpdateGraphic();
        if (this.queue.size() > 5)
            this.fireAddBTS();
    }

    @Override
    public int getNumberWaitingSMS() {
        return super.queue.size();
    }

    @Override
    public Long getDestinationId() {
        return (long)this.number;
    }

    @Override
    public int getNumber() {
        return super.number;
    }

    private void fireAddBTS() {
        for (ScrollPanelListener i : this.listenerScrollArrayList)
            i.addEvent(
                    new ScrollPanelEvent(this)
            );
    }
}


class LastBTSLogic
        extends LogicStation
        implements Runnable, SMSBus, BTSLogicaInterface {

    LastBTSLogic(final List<SMSBus> smsBus) {
        super(smsBus);
        new Thread(this).start();
    }


    private SMSBus getDestination(List<SMSBus> smsBus, SMS sms) {
        synchronized (smsBus) {
            for (SMSBus i : smsBus) {
                if (i.getDestinationId() == sms.getRNumber()) {
                    return i;
                }

            }
            return null;
        }
    }

    @Override
    public void run() {
        while(true) {

            // send to first bsc

            if (this.queue.size() > 0) {
                SMS sendSMS = queue.remove();

                SMSBus bus = getDestination(this.smsBus, sendSMS);
                if (bus != null) {
                    this.smsProcessedNumber++;
                    bus.receive(sendSMS);
                } else {
                    try {
                        throw new Exception("not found: " + sendSMS.getRNumber() + " number");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }


            super.fireUpdateGraphic();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public synchronized void receive(SMS sms) {
        this.queue.add(sms);
        super.fireUpdateGraphic();
        if (this.queue.size() > 5)
            this.fireAddBTS();
    }

    @Override
    public int getNumberWaitingSMS() {
        return super.queue.size();
    }

    @Override
    public Long getDestinationId() {
        return (long) this.number;
    }

    @Override
    public int getNumber() {
        return super.number;
    }

    private void fireAddBTS() {
        for (ScrollPanelListener i : this.listenerScrollArrayList)
            i.addEvent(
                    new ScrollPanelEvent(this)
            );
    }
}

class BTSGraphic
        extends GraphicStation {

    BTSGraphic(BTSLogicaInterface logicBTS) {
        super(logicBTS.getNumber());
    }
}


class VBDLogic
        extends LogicDevice
        implements Runnable, VBDLogicalInterface, SMSBus {

    private final String message;
    private boolean run = true;
    private final List<SMSBus> smsBus;
    private int freq = 500;
    private boolean state = false;

    final ArrayList<Long> VRDid;



    private static final Map<Long, DataContainer> map = new ConcurrentHashMap<>();

    public static Map<Long, DataContainer> getMap() {
        return map;
    }

    VBDLogic(String message, final List<SMSBus> smsBus, final ArrayList<Long> VRDid) {
        this.message = message;
        this.smsBus = smsBus;

        this.VRDid = VRDid;

        map.put(this.getNumber(), new DataContainer(message));

        new Thread(this).start();
    }

    private Long getRandomNumberVRD(final ArrayList<Long> VRDid) {

        synchronized (VRDid) {
            if (VRDid.size() > 0) {
                int index = (int)(Math.random()*VRDid.size());
                return VRDid.get(index);
            }
            else
                return null;
        }

    }

    private SMSBus getLeastLoaded() {

        synchronized (smsBus) {
            if (smsBus.size() > 0) {
                SMSBus temp = smsBus.get(0);
                for (SMSBus i : smsBus) {
                    if (i.getNumberWaitingSMS() < temp.getNumberWaitingSMS())
                        temp = i;
                }
                return temp;
            }
            return null;
        }

    }

    @Override
    public void run() {

        while (run) {

            if (state) {
                try {
                    synchronized (this) {
                        wait();
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            SMSBus bus = getLeastLoaded();
            if (bus != null) {
                Long number = getRandomNumberVRD(VRDid);
                if (number != null) {
                    bus.receive(new SMS(this.getNumber(), number, message));
                    map.get(this.getNumber()).incrementCount();
                }

            }

            try {
                Thread.sleep(freq);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Override
    public void setFreq(int val) {
        this.freq = val;
    }

    @Override
    public void delete() {
        this.run = false;
    }

    @Override
    public synchronized void stopWorking() {
        state = true;
    }

    @Override
    public synchronized void resumeWorking() {
        state = false;
        notify();
    }

    @Override
    public void receive(SMS sms) {
        //
    }

    @Override
    public int getNumberWaitingSMS() {
        return 0; //
    }

    @Override
    public Long getDestinationId() {
        return this.getNumber();
    }
}


class VBDGraphic
        extends GraphicDevice
        implements ChangeListener {

    private final JSlider slider = new JSlider(100,1000, 500);
    private final String[] comboxElements = {"Active", "Waiting"};
    private final JComboBox<String> comboBox= new JComboBox<>(comboxElements);

    private final VBDLogicalInterface logicDevice;

    VBDGraphic(VBDLogicalInterface logicDevice) {
        super(logicDevice.getNumber());

        this.logicDevice = logicDevice;

        slider.setPreferredSize(new Dimension(120,50));
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setMajorTickSpacing(250);
        slider.setMinorTickSpacing(100);

        slider.addChangeListener(this);

        comboBox.addActionListener(e -> {
            if (comboBox.getSelectedItem().equals("Waiting"))
                logicDevice.stopWorking();
            else
                logicDevice.resumeWorking();
        });

        super.button.addActionListener(e -> {
            logicDevice.delete();
            this.fireDelete();
        });

        this.add(super.textField);
        this.add(slider);
        this.add(comboBox);
        this.add(super.button);
    }

    private void fireDelete() {
        for (ScrollPanelListener l : super.scrollListenerArrayList)
            l.deleteEvent(
                    new ScrollPanelEvent(this)
            );
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        logicDevice.setFreq(slider.getValue());
    }
}


class VRDLogic
        extends LogicDevice
        implements SMSBus, VRDLogicalInterface, Runnable {


    private boolean run = true;
    private boolean messageCountDelete = false;
    private Integer messageCount = 0;
    private final Object messageCountSynch = new Object();

    @Override
    public void run() {

        while(run) {

            if (messageCountDelete) {
                synchronized (messageCountSynch) {
                    messageCount = 0;
                    this.fireUpdate();
                }
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }

    VRDLogic() {
        new Thread(this).start();
    }

    @Override
    public synchronized void receive(SMS sms) {
        this.queue.add(sms);

        synchronized (messageCountSynch) {
            messageCount += 1;
        }
        this.fireUpdate();

    }

    @Override
    public int getNumberWaitingSMS() {
        return super.queue.size();
    }

    @Override
    public Long getDestinationId() {
        return this.getNumber();
    }

    @Override
    public void destroy() {
        run = false;
    }

    @Override
    public void setDeleteMessageCount(boolean data) {
        this.messageCountDelete = data;
    }

    //listeners
    private ArrayList<UpdateVRDGraphicListener> listeners = new ArrayList<>();
    public void addListener(UpdateVRDGraphicListener e) {
        listeners.add(e);
    }
    public void deleteListener(UpdateVRDGraphicListener e) {
        listeners.remove(e);
    }

    private synchronized void fireUpdate() {

        synchronized (messageCountSynch) {
            for (UpdateVRDGraphicListener i : listeners)
                i.updateGraphic(new DeviceEvent(this), messageCount);
        }

    }

}

class VRDGraphic
        extends GraphicDevice
        implements UpdateVRDGraphicListener {

    private final JCheckBox checkBox = new JCheckBox();
    private final JLabel jLabel = new JLabel("0");

    private final VRDLogicalInterface vrdLogic;

    VRDGraphic(VRDLogicalInterface vrdLogic) {
        super(vrdLogic.getNumber());

        this.vrdLogic = vrdLogic;

        super.button.addActionListener(e -> {
            vrdLogic.destroy();
            this.fireDelete();
        });

        jLabel.setPreferredSize(new Dimension(60,50));

        checkBox.addActionListener(e -> {
            vrdLogic.setDeleteMessageCount(checkBox.isSelected());
        });

        this.add(super.textField);
        this.add(jLabel);
        this.add(checkBox);
        this.add(super.button);
    }

    @Override
    public void updateGraphic(EventObject e, Integer smsCount) {
        jLabel.setText(smsCount.toString());
        this.validate();
        this.repaint();
    }

    public VRDLogicalInterface getConnectedLogic() {
        return vrdLogic;
    }

    private void fireDelete() {
        for (ScrollPanelListener l : super.scrollListenerArrayList)
            l.deleteEvent(
                    new ScrollPanelEvent(this)
            );
    }

}



class BSCLogic
        extends LogicStation
        implements Runnable, SMSBus, BSCLogicalInterface, UpdateBSC {

    private boolean run = true;

    final Object updateSynchronized = new Object();
    final List<SMSBus> reserveBus;

    BSCLogic(List<SMSBus> smsBus, List<SMSBus> reserveBus) {
        super(smsBus);
        this.reserveBus = reserveBus;
        new Thread(this).start();
    }

    @Override
    public synchronized void receive(SMS sms) {
        this.queue.add(sms);

        synchronized (updateSynchronized) {
            super.fireUpdateGraphic();
        }


        if (this.queue.size() > 5)
            this.fireAddBSC();
    }

    @Override
    public int getNumberWaitingSMS() {
        return this.queue.size();
    }

    @Override
    public Long getDestinationId() {
        return (long)this.number;
    }

    @Override
    public int getNumber() {
        return this.number;
    }

    @Override
    public void delete() {
    }

    @Override
    public void run() {

        while(run) {
            SMSBus bus = this.getLeastLoaded(this.smsBus);

            if (bus != null && this.queue.size() > 0) {
                this.smsProcessedNumber++;
                bus.receive(this.queue.remove());
            }

            synchronized (updateSynchronized) {
                super.fireUpdateGraphic();
            }

            try {
                Thread.sleep( (int)(Math.random() * 10000) + 5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

        while(this.queue.size() > 0) {
            SMSBus bus = this.getLeastLoaded(this.reserveBus);
            bus.receive(this.queue.remove());
        }
    }

    private void fireAddBSC() {
        for (ScrollPanelListener i : this.listenerScrollArrayList)
            i.addEvent(
                    new ScrollPanelEvent(this)
            );
    }

    @Override
    public void setNewSMSBus(List<SMSBus> smsBus) {
        this.smsBus = smsBus;
    }

    @Override
    public void destroy() {
        this.run = false;
    }
}

class BSCGraphic
        extends GraphicStation {

    private final BSCLogicalInterface bscLogic;
    BSCGraphic(BSCLogicalInterface bscLogic) {
        super(bscLogic.getNumber());
        this.bscLogic = bscLogic;
    }

    public BSCLogicalInterface getConnectedBSCLogic() {
        return bscLogic;
    }
}


class BSCPanel
        extends JPanel {

    private List<SMSBus> smsBusSend;
    private final List<SMSBus> smsBusReceive;
    private final ArrayList<UpdateBSC> updateArrayList;
    private final List<SMSBus> reserveBus;

    private class ScrollBSCPanel
            extends JPanel
            implements ScrollPanelListener {

        ScrollBSCPanel() {
            this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        }

        @Override
        public void addEvent(EventObject e) {
            this.addBSC();
        }


        @Override
        public void deleteEvent(EventObject e) {
            smsBusReceive.remove( ((BSCGraphic)e.getSource()).getConnectedBSCLogic() );
            this.remove((BSCGraphic)e.getSource());
            this.refreshPanel();
        }

        public void addBSC() {
            BSCLogic bscLogic = new BSCLogic(smsBusSend, reserveBus);
            BSCGraphic bscGraphic = new BSCGraphic(bscLogic);
            bscLogic.addUpdateListener(bscGraphic);

            synchronized (updateArrayList) {
                updateArrayList.add(bscLogic);
            }

            bscLogic.addScrollListener(this);

            this.add(bscGraphic);

            synchronized (smsBusReceive) {
                smsBusReceive.add(bscLogic);
            }

            this.refreshPanel();
        }

        private void refreshPanel() {
            this.validate();
            this.repaint();
        }

    }


    ScrollBSCPanel scrollBSCPanel;
    BSCPanel(final List<SMSBus> smsBusSend, final List<SMSBus> reserveBus) {
        this.setBackground(new Color(255,255,255));
        this.setLayout(new BorderLayout());

        this.reserveBus = reserveBus;
        this.smsBusSend = smsBusSend;
        this.smsBusReceive = Collections.synchronizedList(new ArrayList<>());
        this.updateArrayList = new ArrayList<>();

        scrollBSCPanel = new ScrollBSCPanel();
        JScrollPane scrollPane = new JScrollPane(scrollBSCPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(150,100));

        // first bsc
        scrollBSCPanel.addBSC();

        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void setNewSmsBus(final List<SMSBus> smsBus) {
        this.smsBusSend = smsBus;

        synchronized (updateArrayList) {
            for (UpdateBSC device : updateArrayList) {
                device.setNewSMSBus(smsBus);
            }
        }

    }
    public final List<SMSBus> getSMSBusArrayPointer() {
        return smsBusReceive;
    }

    public void destroy() {

        synchronized (updateArrayList) {
            for (UpdateBSC i : updateArrayList) {
                i.destroy();
            }
        }

    }

}

class BTSPanel
        extends JPanel {


    private final List<SMSBus> smsBusSend;
    private final List<SMSBus> smsBusReceive;

    private final boolean last;

    private class ScrollBTSPanel
            extends JPanel
            implements ScrollPanelListener {

        ScrollBTSPanel() {
            this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        }

        @Override
        public void addEvent(EventObject e) {
            this.addBTS();
        }

        @Override
        public void deleteEvent(EventObject e) {
            //
        }

        public void addBTS() {

            BTSGraphic btsGraphic;

            if (last) {
                LastBTSLogic btsLogic = new LastBTSLogic(smsBusSend);
                btsGraphic = new BTSGraphic(btsLogic);
                btsLogic.addUpdateListener(btsGraphic);
                btsLogic.addScrollListener(this);

                synchronized (smsBusReceive) {
                    smsBusReceive.add(btsLogic);
                }
            } else {
                BTSLogic btsLogic = new BTSLogic(smsBusSend);
                btsGraphic = new BTSGraphic(btsLogic);
                btsLogic.addUpdateListener(btsGraphic);
                btsLogic.addScrollListener(this);

                synchronized (smsBusReceive) {
                    smsBusReceive.add(btsLogic);
                }
            }


            this.add(btsGraphic);
            this.refreshPanel();
        }

        private void refreshPanel() {
            this.validate();
            this.repaint();
        }

    }

    private ScrollBTSPanel scrollBTSPanel;

    BTSPanel(List<SMSBus> smsBusSend, boolean last) {
        this.setBackground(new Color(255,255,255));
        this.setLayout(new BorderLayout());

        this.last = last;
        this.smsBusSend = smsBusSend;
        this.smsBusReceive = Collections.synchronizedList(new ArrayList<>());

        scrollBTSPanel = new ScrollBTSPanel();
        JScrollPane scrollPane = new JScrollPane(scrollBTSPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(150,100));

        // first bts
        scrollBTSPanel.addBTS();

        this.add(scrollPane, BorderLayout.CENTER);
    }

    public List<SMSBus> getSMSBusArrayPointer() {
        return smsBusReceive;
    }

}



class ReceiverPanel
        extends JPanel {


    private final List<SMSBus> smsBusReceive;
    private final static ArrayList<Long> idList = new ArrayList<>();

    public ArrayList<Long> getIdList() {
        return idList;
    }

    private class ScrollVRDPanel
            extends JPanel
            implements ScrollPanelListener {


        ScrollVRDPanel() {
            this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        }

        @Override
        public void addEvent(EventObject e) {
            this.addVRD();
        }

        @Override
        public void deleteEvent(EventObject e) {

            this.remove((VRDGraphic)e.getSource());

            synchronized (smsBusReceive) {
                smsBusReceive.remove( ((VRDGraphic)e.getSource()).getConnectedLogic() );
            }

            synchronized (idList) {
                idList.remove( ((VRDGraphic)e.getSource()).getConnectedLogic().getNumber() );
            }

            this.refreshPanel();
            refresh();
        }

        public void addVRD() {
            VRDLogic vrdLogic = new VRDLogic();
            VRDGraphic vrdGraphic = new VRDGraphic(vrdLogic);

            vrdLogic.addListener(vrdGraphic);


            synchronized (idList) {
                idList.add(vrdLogic.getNumber());
            }

            vrdGraphic.addScrollListener(this);

            synchronized (smsBusReceive) {
                smsBusReceive.add(vrdLogic);
            }

            this.add(vrdGraphic);
            this.refreshPanel();
            refresh();
        }

        private void refreshPanel() {
            this.validate();
            this.repaint();
        }
    }

    private void refresh() {
        this.validate();
        this.repaint();
    }

    ScrollVRDPanel scrollVRDPanel;
    ReceiverPanel() {

        this.setBackground(new Color(255,255,255));
        this.setLayout(new BorderLayout());

        scrollVRDPanel = new ScrollVRDPanel();
        this.smsBusReceive = Collections.synchronizedList(new ArrayList<>());

        // scroll pane
        JScrollPane scrollPane = new JScrollPane(scrollVRDPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(150,100));

        JButton button = new JButton("add");
        button.addActionListener(e -> {
            scrollVRDPanel.addVRD();
        });

        this.add(scrollPane, BorderLayout.CENTER);
        this.add(button, BorderLayout.SOUTH);
    }

    public List<SMSBus> getSMSBusArrayPointer() {
        return smsBusReceive;
    }

}

class TransmitterPanel
        extends JPanel {

    private final List<SMSBus> smsBusSend;
    private final DialogWindow dialogWindow;
    private final ScrollVBDPanel scrollPanel;
    private final ArrayList<Long> VRDid;

    private class ScrollVBDPanel
            extends JPanel
            implements ScrollPanelListener {

        ScrollVBDPanel() {
            this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        }
        private void refreshPanel() {
            this.validate();
            this.repaint();
        }

        @Override
        public void addEvent(EventObject e) {
            VBDLogic vbdLogic = new VBDLogic(dialogWindow.getText(), smsBusSend, VRDid);
            VBDGraphic vbdGraphic = new VBDGraphic(vbdLogic);
            vbdGraphic.addScrollListener(this);

            this.add(vbdGraphic);
            this.refreshPanel();
            refresh();
        }

        @Override
        public void deleteEvent(EventObject e) {
            this.remove((VBDGraphic)e.getSource());
            this.refreshPanel();
            refresh();
        }
    }

    private static class DialogWindow
            extends JDialog {

        private final JButton button = new JButton("OK");
        private final JTextField textField = new JTextField();
        private String message = "";

        public DialogWindow() {

            this.setLayout(new FlowLayout());
            textField.setPreferredSize(new Dimension(50,50));

            dialogListeners = new ArrayList<>();

            button.addActionListener(e->{
                message = textField.getText();
                this.fireDialogUpdated();
                this.setVisible(false);
            });


            this.add(textField);
            this.add(button);
            this.setSize(150,150);
        }

        @Override
        public void setVisible(boolean b) {
            textField.setText("");
            super.setVisible(b);
        }

        // listeners
        private final ArrayList<ScrollPanelListener> dialogListeners;

        public void addDialogListener(ScrollPanelListener l) {
            this.dialogListeners.add(l);
        }

        public void removeDialogListener(ScrollPanelListener l) {
            this.dialogListeners.remove(l);
        }

        private void fireDialogUpdated() {
            for (ScrollPanelListener l : dialogListeners)
                l.addEvent(
                        new ScrollPanelEvent(this)
                );
        }

        public String getText() {
            return message;
        }

    }

    private void refresh() {
        this.validate();
        this.repaint();
    }

    TransmitterPanel(final List<SMSBus> smsBusSend, final ArrayList<Long> VRDid) {

        this.setBackground(new Color(255,255,255));
        this.setLayout(new BorderLayout());

        this.VRDid = VRDid;

        this.smsBusSend = smsBusSend;

        this.dialogWindow = new DialogWindow();

        scrollPanel = new ScrollVBDPanel();
        dialogWindow.addDialogListener(scrollPanel);

        // scroll pane
        JScrollPane scrollPane = new JScrollPane(scrollPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(150,100));


        // dialog window
        JButton button = new JButton("add");
        button.addActionListener(e -> {
            dialogWindow.setVisible(true);
        });

        this.add(scrollPane, BorderLayout.CENTER);
        this.add(button, BorderLayout.SOUTH);

    }

}


// bts, bsc
class Stations
        extends JPanel {

    private static class BSCsPanel
            extends JPanel {

        private final List<SMSBus> reserveBus;

        private class ScrollBSCPanels
                extends JPanel {

            private final ArrayList<BSCPanel> BSCPanels;
            private final List<SMSBus> smsBusSend;
            private final List<SMSBus> smsBusReceive;


            ScrollBSCPanels(final List<SMSBus> smsBusReceive) {
                this.setLayout(new GridLayout(1,0));

                this.smsBusReceive = smsBusReceive;

                this.BSCPanels = new ArrayList<>();

                // first BSC layer:
                BSCPanel temp = new BSCPanel(smsBusReceive, reserveBus);

                this.smsBusSend = temp.getSMSBusArrayPointer();
                this.BSCPanels.add(temp);
                this.add(temp);
            }

            private BSCPanel getLastPanel() {
                return this.BSCPanels.get(BSCPanels.size()-1);
            }

            void addBSCPanel() {
                BSCPanel temp = new BSCPanel(smsBusReceive, reserveBus);
                getLastPanel().setNewSmsBus(temp.getSMSBusArrayPointer());

                this.BSCPanels.add(temp);
                this.add(temp);
                this.refreshPanel();
                this.refresh();
            }

            private void refreshPanel() {
                this.validate();
                this.repaint();
            }

            void deleteBSCPanel() {

                if (BSCPanels.size() > 1) {
                    BSCPanel temp = BSCPanels.get(BSCPanels.size()-1);
                    this.BSCPanels.remove(temp);
                    this.remove(temp);
                    temp.destroy();

                    synchronized (smsBusReceive) {
                        this.BSCPanels.get(this.BSCPanels.size()-1).setNewSmsBus(smsBusReceive);
                    }

                    this.refreshPanel();
                    this.refresh();
                }

            }

            public List<SMSBus> getSMSBusInputPointer() {
                return smsBusSend;
            }

            JScrollPane scrollPane;
            void refresh() {
                scrollPane.validate();
                scrollPane.repaint();
            }

            void addRefreshable(JScrollPane jScrollPane) {
                this.scrollPane = jScrollPane;
            }

        }


        private final ScrollBSCPanels scrollBSCPanels;

        BSCsPanel(final List<SMSBus> smsBus) { // last bts pointer

            scrollBSCPanels = new ScrollBSCPanels(smsBus);

            this.reserveBus = smsBus;

            JButton buttonAdd = new JButton("+");
            JButton buttonDel = new JButton("-");

            buttonAdd.addActionListener(e -> {
                scrollBSCPanels.addBSCPanel();
            });

            buttonDel.addActionListener(e -> {
                scrollBSCPanels.deleteBSCPanel();
            });

            JPanel buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
            buttonsPanel.add(buttonDel);
            buttonsPanel.add(buttonAdd);



            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            JScrollPane scrollPane = new JScrollPane(scrollBSCPanels);
            scrollBSCPanels.addRefreshable(scrollPane);
            scrollPane.setPreferredSize(new Dimension(150,100));

            this.add(scrollPane);
            this.add(buttonsPanel);

        }

        // first bts (in)
        public List<SMSBus> getSMSBusArrayPointer() {
            return scrollBSCPanels.getSMSBusInputPointer();
        }

    }



    BTSPanel lastBTS;
    BTSPanel firstBTS;
    BSCsPanel BSCsPanel;

    Stations(final List<SMSBus> smsBus) { // sms bus to receiver
        this.setLayout(new GridLayout());

        this.lastBTS = new BTSPanel(smsBus, true);
        this.BSCsPanel = new BSCsPanel(this.lastBTS.getSMSBusArrayPointer());
        this.firstBTS = new BTSPanel(this.BSCsPanel.getSMSBusArrayPointer(), false);

        // first bts layer
        this.add(firstBTS);

        // BSCs panel
        this.add(BSCsPanel);

        // last bts layer
        this.add(lastBTS);

    }


    // receiver, first bts
    List<SMSBus> InputSMSBus() {
        return firstBTS.getSMSBusArrayPointer();
    }

}

public class Main
        extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                Main::new
        );
    }

    Main() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        this.getContentPane().add(mainPanel, BorderLayout.LINE_START);

        // communication pointers: receiver -> stations -> transmitter

        ReceiverPanel receiverPanel = new ReceiverPanel();
        Stations stations = new Stations(receiverPanel.getSMSBusArrayPointer());
        TransmitterPanel transmitterPanel = new TransmitterPanel(stations.InputSMSBus(), receiverPanel.getIdList());


        mainPanel.add(transmitterPanel, BorderLayout.WEST);
        mainPanel.add(stations, BorderLayout.CENTER);
        mainPanel.add(receiverPanel, BorderLayout.EAST);


        this.setSize(500, 500);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                File file = new File("VBDs.bin");
                try {
                    OutputStream stream = new FileOutputStream(file);


                    Map<Long, DataContainer> map = VBDLogic.getMap();
                    Set<Long> keys =  map.keySet();

                    for (Long key : keys) {
                        ArrayList<Byte> list = map.get(key).getBinRepresentation(key);
                        byte[] data = new byte[list.size()];
                        for (int i=0; i < list.size(); i++)
                            data[i] = list.get(i);

                        stream.write(data);
                    }

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                System.exit(0);
            }
        });

        this.setVisible(true);

    }

}