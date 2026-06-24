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
package org.isf.opd.manager;

import java.util.List;

import org.isf.opd.model.TuberculosisContact;
import org.isf.opd.service.TuberculosisContactIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TuberculosisContactManager {

    @Autowired
    private TuberculosisContactIoOperations ioOperations;

    public TuberculosisContact newContact(TuberculosisContact contact) throws OHServiceException {
        return ioOperations.saveContact(contact);
    }

    public TuberculosisContact updateContact(TuberculosisContact contact) throws OHServiceException {
        return ioOperations.updateContact(contact);
    }

    public void deleteContact(TuberculosisContact contact) throws OHServiceException {
        ioOperations.deleteContact(contact);
    }

    public TuberculosisContact getContactById(Integer id) throws OHServiceException {
        return ioOperations.findContactById(id);
    }

    public List<TuberculosisContact> getContactsByTreatmentId(Integer treatmentId) throws OHServiceException {
        return ioOperations.findContactsByTreatmentId(treatmentId);
    }
}
