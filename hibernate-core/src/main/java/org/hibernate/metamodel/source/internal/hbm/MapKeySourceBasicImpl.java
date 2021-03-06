/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.metamodel.source.internal.hbm;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.cfg.NamingStrategy;
import org.hibernate.metamodel.internal.binder.Binder;
import org.hibernate.metamodel.source.internal.jaxb.hbm.JaxbColumnElement;
import org.hibernate.metamodel.source.internal.jaxb.hbm.JaxbIndexElement;
import org.hibernate.metamodel.source.internal.jaxb.hbm.JaxbMapKeyElement;
import org.hibernate.metamodel.source.spi.PluralAttributeMapKeySourceBasic;
import org.hibernate.metamodel.source.spi.RelationalValueSource;
import org.hibernate.metamodel.source.spi.SizeSource;
import org.hibernate.metamodel.spi.PluralAttributeIndexNature;

/**
 *
 */
public class MapKeySourceBasicImpl extends AbstractHbmSourceNode implements PluralAttributeMapKeySourceBasic {
	private final PluralAttributeIndexNature nature;
	private final List<RelationalValueSource> valueSources;
	private final HibernateTypeSourceImpl typeSource;

	public MapKeySourceBasicImpl(MappingDocument sourceMappingDocument, final JaxbMapKeyElement mapKey) {
		super( sourceMappingDocument );

		final String typeName = extractTypeName( mapKey );
		final Map<String, String> typeParams = mapKey.getType() != null
				? Helper.extractParameters( mapKey.getType().getParam() )
				: java.util.Collections.<String, String>emptyMap();
		this.typeSource = new HibernateTypeSourceImpl( typeName, typeParams );

		this.valueSources = Helper.buildValueSources(
				sourceMappingDocument(),
				new Helper.ValueSourcesAdapter() {

					@Override
					public String getColumnAttribute() {
						return mapKey.getColumnAttribute();
					}

					@Override
					public SizeSource getSizeSource() {
						return Helper.createSizeSourceIfMapped(
								mapKey.getLength(),
								null,
								null
						);
					}


					@Override
					public List<JaxbColumnElement> getColumn() {
						return mapKey.getColumn();
					}

					@Override
					public List<String> getFormula() {
						return mapKey.getFormula();
					}

					@Override
					public String getFormulaAttribute() {
						return mapKey.getFormulaAttribute();
					}

					@Override
					public boolean isIncludedInInsertByDefault() {
						return areValuesIncludedInInsertByDefault();
					}

					@Override
					public boolean isIncludedInUpdateByDefault() {
						return areValuesIncludedInUpdateByDefault();
					}
				}
		);

		this.nature = PluralAttributeIndexNature.BASIC;
	}

	private String extractTypeName(JaxbMapKeyElement mapKey) {
		if ( mapKey.getTypeAttribute() != null ) {
			return mapKey.getTypeAttribute();
		}
		if ( mapKey.getType() != null ) {
			return mapKey.getType().getName();
		}
		return null;
	}

	public MapKeySourceBasicImpl(MappingDocument sourceMappingDocument, final JaxbIndexElement indexElement) {
		super( sourceMappingDocument );
		this.typeSource = new HibernateTypeSourceImpl( indexElement.getType() );

		this.valueSources = Helper.buildValueSources(
				sourceMappingDocument,
				new Helper.ValueSourcesAdapter() {

					@Override
					public String getColumnAttribute() {
						return indexElement.getColumnAttribute();
					}

					@Override
					public SizeSource getSizeSource() {
						return Helper.createSizeSourceIfMapped(
								indexElement.getLength(),
								null,
								null
						);
					}


					@Override
					public List<JaxbColumnElement> getColumn() {
						return indexElement.getColumn();
					}

					@Override
					public boolean isIncludedInInsertByDefault() {
						return areValuesIncludedInInsertByDefault();
					}

					@Override
					public boolean isIncludedInUpdateByDefault() {
						return areValuesIncludedInUpdateByDefault();
					}
				}
		);

		this.nature = PluralAttributeIndexNature.BASIC;
	}

	@Override
	public boolean areValuesIncludedInInsertByDefault() {
		return true;
	}

	@Override
	public boolean areValuesIncludedInUpdateByDefault() {
		return true;
	}

	@Override
	public boolean areValuesNullableByDefault() {
		return false;
	}

	@Override
	public PluralAttributeIndexNature getNature() {
		return nature;
	}

	@Override
	public List<Binder.DefaultNamingStrategy> getDefaultNamingStrategies() {
		final Binder.DefaultNamingStrategy defaultNamingStrategy =
				new Binder.DefaultNamingStrategy() {
					@Override
					public String defaultName(NamingStrategy namingStrategy) {
						return namingStrategy.columnName( "idx" );
					}
				};
		return Collections.singletonList( defaultNamingStrategy );
	}

	@Override
	public HibernateTypeSourceImpl getTypeInformation() {
		return typeSource;
	}

	@Override
	public List<RelationalValueSource> relationalValueSources() {
		return valueSources;
	}
}
