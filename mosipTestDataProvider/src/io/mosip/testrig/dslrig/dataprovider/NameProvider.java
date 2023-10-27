package io.mosip.testrig.dslrig.dataprovider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mosip.testrig.dslrig.dataprovider.models.Name;
import io.mosip.testrig.dslrig.dataprovider.util.CommonUtil;
import io.mosip.testrig.dslrig.dataprovider.util.Gender;
import io.mosip.testrig.dslrig.dataprovider.util.Translator;
import io.mosip.testrig.dslrig.dataprovider.variables.VariableManager;

public class NameProvider {
	private static final Logger logger = LoggerFactory.getLogger(NameProvider.class);
	private static String resourceName_male ;
	//= VariableManager.getVariableValue(VariableManager.NS_DEFAULT,"mosip.test.persona.namesdatapath").toString()+"/%s/boy_names.csv";
	private static String resourceName_female ;
	//= VariableManager.getVariableValue(VariableManager.NS_DEFAULT,"mosip.test.persona.namesdatapath").toString()+"/%s/girl_names.csv";
	private static String resourceName_surname;
	//=VariableManager.getVariableValue(VariableManager.NS_DEFAULT,"mosip.test.persona.namesdatapath").toString()+"/%s/surnames.csv";
	
	static String[] getSurNames(String lang, int count,String contextKey) {
		resourceName_surname =VariableManager.getVariableValue(contextKey,"mountPath").toString()+
				VariableManager.getVariableValue(contextKey,"mosip.test.persona.namesdatapath").toString()+"/"+VariableManager.getVariableValue(contextKey,"langCode").toString()+"/surnames.csv";
		
		String resPath = String.format(resourceName_surname, lang);
		String [] values = new String[count];
		int i=0;
		try {
			CSVHelper helper;
			helper = new CSVHelper(resPath);
			int recCount = 200; // helper.getRecordCount();
			int[] recNos = CommonUtil.generateRandomNumbers(count, recCount,0);
		
			helper.open();
			List<String[]> recs = helper.readRecords(recNos);
			for(String[] r: recs) {
				if(lang.equals("en"))
					values[i] = CommonUtil.toCaptialize(r[0]);
				else
					values[i] =  r[0];
				
				i++;
			}
			
			helper.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return values;
		
	}
	public static List<Name> generateNames(Gender gender,String lang, int count,List<Name> engNames,String contextKey ){
	
		List<Name> names = null;
		if(engNames == null) 
			names = generateNamesWrapper(gender,  count,contextKey);
		else names = engNames;
			
		if(!lang.startsWith("en")) {
			List<Name> namesLang = new ArrayList<Name>();
			for(Name name: names) {
				Name langName = Translator.translateName( lang, name,contextKey);
				namesLang.add(langName);
			}
			names = namesLang;
		}
		
		return names;
	}
	static List<Name> generateNamesWrapper(Gender gender, int count,String contextKey){
		/*
		syntheticnames=true
				syntheticmidname=true

				syntheticfirstnamelen=50
				syntheticmidnamelen=50
				syntheticlastnamelen=50
		 */
		Object objAttr = VariableManager.getVariableValue(VariableManager.NS_DEFAULT,"syntheticnames");
		boolean bValue = objAttr == null ? false :  Boolean.parseBoolean(objAttr.toString());
		if(bValue) {
		
			return generateSynthNames(gender, count);
		}
		else
		{
			return generateNames(gender,count,contextKey);
		}
		
	}
	static String genRandomWord(int len) {
		String w = new String("");
		
		int[] charName = CommonUtil.generateRandomNumbers(len, (int)'z',(int)'a');
		for(int c: charName )
			w +=  (char)c;
		
		return w;
	}
	static List<Name> generateSynthNames(Gender gender, int count){
		String lang ="en"; 
		List<Name> names = new ArrayList<Name>();
		Object objAttr = VariableManager.getVariableValue(VariableManager.NS_DEFAULT,"syntheticmidname");
		boolean bValue = objAttr == null ? false : Boolean.parseBoolean(objAttr.toString());
		objAttr = VariableManager.getVariableValue(VariableManager.NS_DEFAULT,"syntheticfirstnamelen");
		int fNameLen = objAttr == null ? 30: Integer.parseInt(objAttr.toString());
	
		objAttr = VariableManager.getVariableValue(VariableManager.NS_DEFAULT,"syntheticmidnamelen");
		
		int mNameLen = objAttr == null ? 30: Integer.parseInt(objAttr.toString());
		
		objAttr = VariableManager.getVariableValue(VariableManager.NS_DEFAULT,"syntheticlastnamelen");
		int lNameLen = objAttr == null ? 30: Integer.parseInt(objAttr.toString());
		
		for(int i=0; i < count; i++) {
			Name n = new Name();
			n.setFirstName( genRandomWord(30));
			if(bValue)
				n.setMidName( genRandomWord(30));
			n.setSurName( genRandomWord(30));
			n.setGender(gender);
			names.add(n);
		}
		
		return names;
		
	}
	 
	static List<Name> generateNames(Gender gender, int count,String contextKey){
	
		String lang ="en"; 
		List<Name> names = new ArrayList<Name>();
		
		String resPath = "";
		Gender recGender = Gender.Female;
		
		if(gender == Gender.Male) {
		 resourceName_male = VariableManager.getVariableValue(contextKey,"mountPath").toString()+VariableManager.getVariableValue(contextKey,"mosip.test.persona.namesdatapath").toString()+"/"+VariableManager.getVariableValue(contextKey,"langCode").toString()+"/boy_names.csv";
			
			resPath = String.format(resourceName_male, lang);
			recGender = Gender.Male;
		}
		else
		{
			resourceName_female = VariableManager.getVariableValue(contextKey,"mountPath").toString()+VariableManager.getVariableValue(contextKey,"mosip.test.persona.namesdatapath").toString()+"/"+VariableManager.getVariableValue(contextKey,"langCode").toString()+"/girl_names.csv";
			resPath = String.format(resourceName_female, lang);
		}		
		try {
			CSVHelper helper;
			
			helper = new CSVHelper(resPath);
		
			int recCount = helper.getRecordCount();
			int[] recNos = CommonUtil.generateRandomNumbers(count, recCount,0);
			helper.open();
			
			List<String[]> recs = helper.readRecords( recNos);
			
			String[] surNames = getSurNames(lang, count,contextKey); 
			Name name = new Name();
			int i=0;
			for(String[] r: recs) {
				if(lang.equals("en")) {
					name.setFirstName(CommonUtil.toCaptialize(r[1]));
					name.setSurName( CommonUtil.toCaptialize(surNames[i]));
				}
				else {
					name.setFirstName(r[1]);
					name.setSurName(surNames[i]);
				}
				name.setGender(recGender);
				names.add(name);
				i++;
			}
			helper.close();
		
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return names;
	}
}
