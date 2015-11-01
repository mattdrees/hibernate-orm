package org.hibernate.cfg;

import org.hibernate.mapping.PersistentClass;

import java.util.Map;

/**
 * @author Matt Drees
 */
public interface DependentSecondPass extends SecondPass {

	boolean dependentUpon(SecondPass secondPass, Map<String, PersistentClass> persistentClasses);
}
