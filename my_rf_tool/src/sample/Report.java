package sample;

import javafx.beans.property.SimpleStringProperty;

public class Report {

    private static String pid, piddesc;

    Report(String pid, String piddesc){
        this.pid = new String(pid);
        this.piddesc = new String(piddesc);
    }

    public String getpid(){
        return pid;
    }

    public void setpid(String pid){
        this.pid = pid;
    }

    public String getpiddesc(){
        return piddesc;
    }

    public void setpiddesc(String piddesc){
        this.piddesc = piddesc;
    }
}
