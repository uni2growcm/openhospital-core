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
package org.isf.exa.model;

import java.util.Objects;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Association between an exam block and one of its exams (many-to-many link table).
 */
@Entity
@Table(name = "OH_BLOCK_EXAM")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "BLKEX_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "BLKEX_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "BLKEX_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "BLKEX_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "BLKEX_LAST_MODIFIED_DATE"))
public class BlockExam extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BLKEX_ID")
	private int id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "BLKEX_BLK_ID_A")
	private Block block;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "BLKEX_EXA_ID_A")
	private Exam exam;

	@Transient
	private volatile int hashCode;

	public BlockExam() {
		super();
	}

	public BlockExam(Block block, Exam exam) {
		super();
		this.block = block;
		this.exam = exam;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public Exam getExam() {
		return exam;
	}

	public void setExam(Exam exam) {
		this.exam = exam;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BlockExam other)) {
			return false;
		}
		return id == other.id
				&& Objects.equals(block, other.block)
				&& Objects.equals(exam, other.exam);
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			final int m = 23;
			int c = 133;
			c = m * c + Objects.hashCode(block);
			c = m * c + Objects.hashCode(exam);
			this.hashCode = c;
		}
		return this.hashCode;
	}
}
