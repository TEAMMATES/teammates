package teammates.e2e.cases.lnp;

import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.SampleSaveConfiguration;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.Calculator;

import java.io.PrintWriter;

public class MyResultCollector extends ResultCollector {
    private Calculator loginCalculator = new Calculator("Login");
    private Calculator endpointCalculator = new Calculator("Test Endpoint");
    private transient volatile PrintWriter out;

    public MyResultCollector(Summariser summer) {
        super(summer);
    }

    @Override
    public void sampleOccurred(SampleEvent e) {
        super.sampleOccurred(e);
        SampleResult r = e.getResult(); // each sample result refers to one value, cannot find for all
        r.getSampleLabel();

        if (r.getSampleLabel().equals("Login")) {
            loginCalculator.addSample(r);
        } else if (r.getSampleLabel().equals("Test Endpoint")) {
            endpointCalculator.addSample(r);
        }

        if (isSampleWanted(r.isSuccessful())) {
            SampleSaveConfiguration config = getSaveConfig();
            r.setSaveConfig(config);
            try {
                if (config.saveAsXml()) {
                    SaveService.saveSampleResult(e, out);
                }
            } catch (Exception err) {
                System.out.println("Error trying to record a sample " + err);
            }
        }
    }

    public Calculator getEndpointCalculator() {
        return endpointCalculator;
    }

    public Calculator getLoginCalculator() {
        return loginCalculator;
    }
}
