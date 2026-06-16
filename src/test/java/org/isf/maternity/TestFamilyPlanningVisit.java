/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.maternity;

import org.isf.maternity.model.FPVisitType;
import org.isf.maternity.model.FamilyPlanning;
import org.isf.maternity.model.FamilyPlanningVisit;

import java.time.LocalDateTime;

public class TestFamilyPlanningVisit {

    private final LocalDateTime visitDate = LocalDateTime.of(2025, 7, 15, 10, 30);

    public FamilyPlanningVisit setup(FamilyPlanning fp, boolean usingSet) {
        FamilyPlanningVisit visit;
        if (usingSet) {
            visit = new FamilyPlanningVisit();
        } else {
            visit = new FamilyPlanningVisit(fp, visitDate, FPVisitType.FOLLOWUP);
        }
        setParameters(visit, fp);
        return visit;
    }

    private void setParameters(FamilyPlanningVisit visit, FamilyPlanning fp) {
        visit.setFamilyPlanning(fp);
        visit.setVisitDate(visitDate);
        visit.setVisitType(FPVisitType.FOLLOWUP);
        visit.setComplaints("No complaints");
    }

    public void check(FamilyPlanningVisit visit) {
        assert visit.getVisitType() == FPVisitType.FOLLOWUP;
        assert visit.getVisitDate().equals(visitDate);
        assert visit.getComplaints().equals("No complaints");
    }
}
