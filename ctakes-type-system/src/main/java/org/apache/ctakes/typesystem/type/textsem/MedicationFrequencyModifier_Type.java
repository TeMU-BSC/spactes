
/* First created by JCasGen Fri Jun 07 10:58:42 CEST 2019 */
package org.apache.ctakes.typesystem.type.textsem;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

/** How often a medication should be used. e.g., BID or b.i.d. or "twice-a-day" would have a number=2 and unit=day.
 * Updated by JCasGen Thu Jun 20 11:44:40 CEST 2019
 * @generated */
public class MedicationFrequencyModifier_Type extends Modifier_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = MedicationFrequencyModifier.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.apache.ctakes.typesystem.type.textsem.MedicationFrequencyModifier");



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public MedicationFrequencyModifier_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    