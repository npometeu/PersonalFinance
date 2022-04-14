package PersonalFinance.saveload;

import PersonalFinance.settings.Settings;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class SaveLoad {

    public static void load(SaveData sd) {
        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(Wrapper.class);
            Unmarshaller um = context.createUnmarshaller();
            Wrapper wrapper =  (Wrapper)um.unmarshal(Settings.getFileSave());
            if (wrapper.getAccounts() != null) sd.setAccounts(wrapper.getAccounts());
            if (wrapper.getArticles() != null) sd.setArticles(wrapper.getArticles());
            if (wrapper.getTransactions() != null) sd.setTransactions(wrapper.getTransactions());
            if (wrapper.getTransfers() != null) sd.setTransfers(wrapper.getTransfers());
            if (wrapper.getCurrencies() != null) sd.setCurrencies(wrapper.getCurrencies());
//            sd.setAccounts(wrapper.getAccounts());
//            sd.setArticles(wrapper.getArticles());
//            sd.setTransactions(wrapper.getTransactions());
//            sd.setTransfers(wrapper.getTransfers());
//            sd.setCurrencies(wrapper.getCurrencies());
        } catch (JAXBException e) {
            System.out.println("Файл не существует");
        }
    }

    public static void save(SaveData sd) {
        try {
            JAXBContext context = JAXBContext.newInstance(Wrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            Wrapper wrapper = new Wrapper();
            wrapper.setAccounts(sd.getAccounts());
            wrapper.setArticles(sd.getArticles());
            wrapper.setTransactions(sd.getTransactions());
            wrapper.setTransfers(sd.getTransfers());
            wrapper.setCurrencies(sd.getCurrencies());

            m.marshal(wrapper, Settings.getFileSave());

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }


}
