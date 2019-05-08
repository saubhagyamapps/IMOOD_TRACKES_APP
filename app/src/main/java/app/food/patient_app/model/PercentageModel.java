package app.food.patient_app.model;

public class PercentageModel {

    /**
     * timespent : 65
     * worktime : 100
     */

    private long socialmediatime;
    private long worktime;
    private long callduration;

    public long getCallduration() {
        return callduration;
    }

    public void setCallduration(long callduration) {
        this.callduration = callduration;
    }

    public long getSocialmediatime() {
        return socialmediatime;
    }

    public void setSocialmediatime(long socialmediatime) {
        this.socialmediatime = socialmediatime;
    }

    public long getWorktime() {
        return worktime;
    }

    public void setWorktime(long worktime) {
        this.worktime = worktime;
    }
}
