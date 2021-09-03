package ch.pandemieende.updater;

import edu.umd.cs.findbugs.annotations.NonNull;

import edu.umd.cs.findbugs.annotations.Nullable;

import java.time.LocalDate;

public final class Data implements Comparable<Data> {

    private @NonNull final LocalDate statusDate;

    private @Nullable Double vaccinationRate;
    private @Nullable Integer vaccinatedPersons;
    private @Nullable Integer administeredVaccineDoses;

    public Data(@NonNull final LocalDate statusDate) {
        this.statusDate = statusDate;
    }

    @NonNull
    public LocalDate getStatusDate() {
        return statusDate;
    }

    @Nullable
    public Double getVaccinationRate() {
        return vaccinationRate;
    }

    public void setVaccinationRate(@NonNull final Double vaccinationRate) {
        this.vaccinationRate = vaccinationRate;
    }

    @Nullable
    public Integer getVaccinatedPersons() {
        return vaccinatedPersons;
    }

    public void setVaccinatedPersons(@NonNull final Integer vaccinatedPersons) {
        this.vaccinatedPersons = vaccinatedPersons;
    }

    @Nullable
    public Integer getAdministeredVaccineDoses() {
        return administeredVaccineDoses;
    }

    public void setAdministeredVaccineDoses(@NonNull final Integer administeredVaccineDoses) {
        this.administeredVaccineDoses = administeredVaccineDoses;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var data = (Data) o;

        return statusDate.equals(data.statusDate);
    }

    @Override
    public int hashCode() {
        return statusDate.hashCode();
    }

    @Override
    public String toString() {
        return "Data{"
                + "statusDate=" + statusDate
                + ", vaccinationRate=" + vaccinationRate
                + ", vaccinatedPersons=" + vaccinatedPersons
                + ", administeredVaccineDoses=" + administeredVaccineDoses
                + '}';
    }

    @Override
    public int compareTo(final Data data) {
        assert data != null;
        return statusDate.compareTo(data.statusDate);
    }

}
