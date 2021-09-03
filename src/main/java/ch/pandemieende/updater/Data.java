/*
 * MIT License
 *
 * Copyright (C) 2021 Marcus Fihlon and the individual contributors to
 * Pandemieende Updater.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
