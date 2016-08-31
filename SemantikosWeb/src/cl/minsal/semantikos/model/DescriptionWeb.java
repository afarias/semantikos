package cl.minsal.semantikos.model;

public class DescriptionWeb extends Description {

    public boolean hasBeenModified;

    public DescriptionWeb(Description d) {
        super(d.getDescriptionId(), d.getDescriptionType(), d.getTerm(), d.isCaseSensitive(), d.isAutogeneratedName(), d.isActive(), d.isPublished(), d.getValidityUntil());
        this.hasBeenModified = false;
    }

    public DescriptionWeb(long id, Description d) {
        this(d);
        this.setId(id);
    }

    public boolean hasBeenModified() {
        return hasBeenModified;
    }

    public void setModified(boolean hasBeenModified) {
        this.hasBeenModified = hasBeenModified;
    }


    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Description)) {
            return false;
        }
        DescriptionWeb descriptionWeb = (DescriptionWeb) o;
        return this.getTerm().equals(descriptionWeb.getTerm()) &&
               this.isCaseSensitive()==descriptionWeb.isCaseSensitive() &&
               this.isAutogeneratedName()==descriptionWeb.isAutogeneratedName() &&
               this.getDescriptionType().equals(descriptionWeb.getDescriptionType());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getTerm().hashCode();
        result = 31 * result + new Boolean(isCaseSensitive()).hashCode();
        result = 31 * result + new Boolean(isAutogeneratedName()).hashCode();
        result = 31 * result + getDescriptionType().hashCode();
        return result;
    }
}
