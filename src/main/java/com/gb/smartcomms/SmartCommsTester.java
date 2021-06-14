package com.gb.smartcomms;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;

public class SmartCommsTester {

    public static void main(String[] args) throws Exception {

        if (args != null && args.length > 0) {

            System.out.println("arg [" + args[0] + "]");

            if (args[0].equals("SUBMIT")) {

                String configFile = System.getProperty("configfile");
                System.out.println("Loading config file : " + configFile);

                SmartCommsRESTSupport smartSupport = new SmartCommsRESTSupport();
                Properties properties = smartSupport.loadProperties(configFile);

                // load the test xml file
                String str = IOUtils.toString(SmartCommsTester.class.getClassLoader().getResourceAsStream((String) properties.get("testfile")));

                // base64 the payload
                String transxml = smartSupport.createTransactXML(str);

                // wrap into json request for smartcomms
                JSONObject obj = smartSupport.generateDraftRequest(transxml);
                //JSONObject obj = smartSupport.createSubmissionRequest(transxml);
                // send to smart comms
                String resp = smartSupport.sendGenerateDraftRequest(obj);


                System.out.println("SmartComms Draft : " + resp);

                //smartSupport.saveAndSubmitDraft(null);
                String decodedResponse = new String(Base64.getDecoder().decode(resp), StandardCharsets.UTF_8);

                System.out.println(decodedResponse);
            }

            if (args[0].equals("LIST")) {

                String configFile = System.getProperty("configfile");
                System.out.println("Loading config file : " + configFile);

                SmartCommsRESTSupport smartSupport = new SmartCommsRESTSupport();
                Properties properties = smartSupport.loadProperties(configFile);

                smartSupport.callAPIRestEndpoint();

            }

        }


    }

}
