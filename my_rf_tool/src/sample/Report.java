package sample;

import javafx.beans.property.SimpleStringProperty;

/** if you add functions here, make sure functions are CAMEL CASE-- property value factory needs this **/
public class Report {

    private final SimpleStringProperty pid, piddesc;


    Report(String pid, String piddesc){
        this.pid = new SimpleStringProperty(pid);
        this.piddesc = new SimpleStringProperty(piddesc);
    }

    public final String getPid(){
        return pid.get();
    }

    public final void setPid(String pid){
        this.pid.set(pid);
    }

    public final String getPiddesc(){
        return piddesc.get();
    }

    public final void setPiddesc(String piddesc){
        this.piddesc.set(piddesc);
    }
}
