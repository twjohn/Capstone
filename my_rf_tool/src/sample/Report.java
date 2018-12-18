package sample;

import javafx.beans.property.SimpleStringProperty;

/** if you add values here, make sure functions are CAMEL CASE-- property value factory needs this **/
public class Report {

    private repStageController myCount;
    private final SimpleStringProperty pid, piddesc;


    Report(int num, String pid, String piddesc){
        this.myCount.count = 0;
        this.pid = new SimpleStringProperty(pid);
        this.piddesc = new SimpleStringProperty(piddesc);
    }

    public final int getCount(){
        System.out.print(myCount.count);
        return myCount.count+=1;
    }

    public final void setCount(int num){

        this.myCount.count = num;
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
