/*
 * Copyright (c) 2013 Juniper Networks, Inc.
 * All Rights Reserved
 *
 * Use is subject to license terms.
 *
 */

import net.juniper.netconf.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;


public class EditTrapGroupConfiguration {
    public static void main(String[] args) throws IOException
            , ParserConfigurationException, SAXException {


        XMLBuilder builder = new XMLBuilder();


        //XML trapGroupConfig = addTrapGroup(builder);
        XML trapGroupConfig = removeTrapGroup(builder);


        //Create the device
        Device device = new Device("10.127.11.101", "admin", "Lab123!", null);
        device.connect();

        //Lock the configuration first
        boolean isLocked = device.lockConfig();
        if (!isLocked) {
            System.out.println("Could not lock configuration. Exit now.");
            return;
        }

        //Load and commit the configuration
        try {
            device.loadXMLConfiguration(trapGroupConfig.toString(), "merge");
            device.commit();
            System.out.println("code committed");
        } catch (LoadException e) {
            System.out.println(e.getMessage());
            return;
        } catch (CommitException e) {
            System.out.println(e.toString());
            return;
        }

        //Unlock the configuration and close the device.
        device.unlockConfig();
        device.close();
    }


    private static XML removeTrapGroup(XMLBuilder builder) {
        XML trapGroupConfig = builder.createNewConfig("snmp");
        XML trapGroup = trapGroupConfig.addPath("trap-group");
        trapGroup.append("group-name", "psm");
        XML targets = trapGroup.append("targets");
        targets.setAttribute("operation", "delete");
        targets.append("name", "172.27.5.230");
        return trapGroupConfig;
    }

    private static XML addTrapGroup(XMLBuilder builder) {
        XML trapGroupConfig = builder.createNewConfig("snmp");
        XML trapGroup = trapGroupConfig.addPath("trap-group");
        trapGroup.append("group-name", "psm");
        XML categories = trapGroup.append("categories");
        categories.append("chassis");
        categories.append("link");
        trapGroup.append("destination-port", "1620");
        XML targets = trapGroup.append("targets");
        targets.append("name", "172.27.5.230");
        return trapGroupConfig;
    }
}
