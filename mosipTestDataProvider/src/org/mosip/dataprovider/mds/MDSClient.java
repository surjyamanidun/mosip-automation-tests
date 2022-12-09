package org.mosip.dataprovider.mds;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mosip.dataprovider.models.IrisDataModel;
import org.mosip.dataprovider.models.JWTTokenModel;
import org.mosip.dataprovider.models.ResidentModel;
import org.mosip.dataprovider.models.mds.MDSDevice;
import org.mosip.dataprovider.models.mds.MDSDeviceCaptureModel;
import org.mosip.dataprovider.models.mds.MDSRCaptureModel;
import org.mosip.dataprovider.util.CommonUtil;
import org.mosip.dataprovider.util.DataProviderConstants;
import org.mosip.dataprovider.util.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.messages.internal.com.google.common.io.Files;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import variables.VariableManager;

public class MDSClient implements MDSClientInterface {

	int port;
	public static String MDSURL = "http://127.0.0.1:";
	
	public MDSClient(int port) {
		if(port == 0)
			this.port = 4501;
		else
			this.port = port;
	}
	
	
	//create profile folder and create all ISO images as per resident data
	
	public void createProfileOld(String profilePath,String profile, ResidentModel resident) throws Exception {
		File profDir = new File(profilePath + "/"+ profile);
		if(!profDir.exists())
			profDir.mkdir();
		//copy from default profile
		File defProfile = new File( profilePath +"/"+ "Default");
		
		File []defFiles = defProfile.listFiles();
		for(File f: defFiles) {
			try {
				Files.copy(f, new File(profDir.getAbsolutePath() +"\\"+ f.getName()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ISOConverter convert = new ISOConverter();
		try {
			if(!resident.getSkipFace()) {
				byte[] face = resident.getBiometric().getRawFaceData();
				convert.convertFace(face,profDir + "/" + "Face.iso");
			}
			if(!resident.getSkipIris()) {
				
				IrisDataModel iris = resident.getBiometric().getIris();
				if(iris != null) {
					
					if(iris.getRawLeft() != null)
						convert.convertIris(iris.getRawLeft(), profDir + "/"+ "Left_Iris.iso", "Left");
					if(iris.getRawRight() != null)
						convert.convertIris(iris.getRawRight(), profDir + "/"+ "Right_Iris.iso", "Right");
				}
			}
			if(!resident.getSkipFinger()) {
				byte[] [] fingerData = resident.getBiometric().getFingerRaw();
				for(int i=0; i < 10; i++) {
					String fingerName = DataProviderConstants.displayFingerName[i];
					String outFileName = DataProviderConstants.MDSProfileFingerNames[i];
					if(fingerData[i] != null) {
						convert.convertFinger(fingerData[i], profDir + "/" + outFileName + ".iso" , fingerName);
					}
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
	}
	
	
	

	//create profile folder and create all ISO images as per resident data
	
	public void createProfile(String profilePath,String profile, ResidentModel resident,String contextKey) throws Exception {
		File profDir = new File(profilePath + "/"+ profile);
		if(!profDir.exists())
			profDir.mkdir();
		//copy from default profile
		
		
		/////////
		//reach cached finger prints from folder
				String dirPath = VariableManager.getVariableValue(contextKey,"mosip.test.persona.fingerprintdatapath").toString();
				System.out.println("dirPath " + dirPath);
				Hashtable<Integer, List<File>> tblFiles = new Hashtable<Integer, List<File>>();
				File dir = new File(dirPath);

				File listDir[] = dir.listFiles();
				int numberOfSubfolders = listDir.length;

				int min=1;
				int max=numberOfSubfolders ;
				int randomNumber = (int) (Math.random()*(max-min)) + min;
				String beforescenario=VariableManager.getVariableValue(contextKey,"scenario").toString();
				String afterscenario=beforescenario.substring(0, beforescenario.indexOf(':'));

				int currentScenarioNumber = Integer.valueOf(afterscenario);


				// If the available impressions are less than scenario number, pick the random one

				// otherwise pick the impression of same of scenario number
				int impressionToPick = (currentScenarioNumber < numberOfSubfolders) ? currentScenarioNumber : randomNumber ;

				System.out.println("currentScenarioNumber=" + currentScenarioNumber +" numberOfSubfolders=" + numberOfSubfolders + " impressionToPick=" + impressionToPick );
				List<File> lst=new LinkedList<File>();
				for(int i=min; i <= max; i++) {

					lst = CommonUtil.listFiles(dirPath +
							String.format("/Impression_%d/fp_1/", i));
					tblFiles.put(i,lst);
				}
				
				List<File> firstSet = tblFiles.get(impressionToPick);
				System.out.println("Impression used "+ impressionToPick);
		
		///////////////
		
				
		//File defProfile = new File( profilePath +"/"+ "Automatic");
		
		//File []defFiles = defProfile.listFiles();
		for(File f: firstSet) {
			try {
				Files.copy(f, new File(profDir.getAbsolutePath() +"\\"+ f.getName()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ISOConverter convert = new ISOConverter();
		try {
			if(!resident.getSkipFace()) {
				byte[] face = resident.getBiometric().getRawFaceData();
				convert.convertFace(face,profDir + "/" + "Face.iso");
			}
			if(!resident.getSkipIris()) {
				
				IrisDataModel iris = resident.getBiometric().getIris();
				if(iris != null) {
					
					if(iris.getRawLeft() != null)
						convert.convertIris(iris.getRawLeft(), profDir + "/"+ "Left_Iris.iso", "Left");
					if(iris.getRawRight() != null)
						convert.convertIris(iris.getRawRight(), profDir + "/"+ "Right_Iris.iso", "Right");
				}
			}
			if(!resident.getSkipFinger()) {
				byte[] [] fingerData = resident.getBiometric().getFingerRaw();
				for(int i=0; i < 10; i++) {
					String fingerName = DataProviderConstants.displayFingerName[i];
					String outFileName = DataProviderConstants.MDSProfileFingerNames[i];
					if(fingerData[i] != null) {
						convert.convertFinger(fingerData[i], profDir + "/" + outFileName + ".iso" , fingerName);
					}
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
	}
	
	
	public void removeProfile(String profilePath,String profile) {
		setProfile("Default");
		File profDir = new File(profilePath + "/"+ profile);
		if(profDir.exists()) {
			 // list all the files in an array
		      File[] files = profDir.listFiles();

		      // delete each file from the directory
		      for(File file : files) {
		        file.delete();
		      }
		      profDir.delete();
		}
		
	}
	public  void setProfile(String profile) {
		
		String url =  MDSURL +port + "/profile";
		JSONObject body = new JSONObject();
		body.put("profileId", profile);

		try {
			HttpRCapture capture = new HttpRCapture(url);
			capture.setMethod("SETPROFILE");
			String response = RestClient.rawHttp(capture, body.toString());
			JSONObject respObject = new JSONObject(response);

		}catch(Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}

	}
	//Type ->"Finger", "Iris", "Face"
	public  List<MDSDevice> getRegDeviceInfo(String type) {
		
		List<MDSDevice> devices = null;
		
		String url =  MDSURL + port;
		JSONObject body = new JSONObject();
		body.put("type", type);
		Response response = given()
				.contentType(ContentType.JSON)
				.body(body.toString())
				.post(url );
		if(response.getStatusCode() == 200) {
			String resp = response.getBody().asString();
			
			if(resp != null) {
				JSONArray deviceArray = new JSONArray(resp);
				ObjectMapper objectMapper = new ObjectMapper();
				
				try {
					devices = objectMapper.readValue(deviceArray.toString(), 
							objectMapper.getTypeFactory().constructCollectionType(List.class, MDSDevice.class));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    	return devices;
	}
	public  MDSRCaptureModel captureFromRegDevice(MDSDevice device, 
			MDSRCaptureModel rCaptureModel,
			String type,
			String bioSubType, int reqScore,String deviceSubId) {
		String mosipVersion=null;;
		try {
	      mosipVersion=VariableManager.getVariableValue(VariableManager.NS_DEFAULT,"mosip.version").toString();
		}catch(Exception e) {
			
		}

		if(rCaptureModel == null)
			rCaptureModel = new MDSRCaptureModel();

		String url =  MDSURL +port + "/capture";
		JSONObject jsonReq = new JSONObject();
		jsonReq.put("env", "Developer");
		jsonReq.put("purpose", "Registration");
		jsonReq.put("specVersion", "0.9.5");
		jsonReq.put("timeout", "120000");
		jsonReq.put("captureTime", CommonUtil.getUTCDateTime(null));
		jsonReq.put("domainUri", "automated");
		jsonReq.put("transactionId", "123456789123");
		JSONObject bio = new JSONObject();
		bio.put("type", type);
	
		
		
		bio.put("count", 1);
		bio.put("deviceSubId", deviceSubId);
	
		if(type.equalsIgnoreCase("finger")) {

			switch(deviceSubId)
			{
				case "1":
					bio.put("count", 4);
					
					break;
				case "2":
					bio.put("count", 4);
				
					break;
				case "3": 
						bio.put("count", 2);
						break;
			}
			
		}
	
		bio.put("requestedScore", reqScore);
		//bio.put("deviceId", Integer.valueOf(device.getDeviceId()));
		bio.put("deviceId", device.getDeviceId());
		
	
		JSONArray arr = new JSONArray();
		arr.put(bio);
		jsonReq.put("bio", arr);
	/*
		Response response = given()
				.contentType(ContentType.JSON)
				.body(jsonReq.toString())
				.post(url );
	*/
		try {
			HttpRCapture capture = new HttpRCapture(url);
			capture.setMethod("RCAPTURE");
			String response = RestClient.rawHttp(capture, jsonReq.toString());
			System.out.println("MDS RESPONSE :"+  response);
			JSONObject respObject = new JSONObject(response);
			JSONArray bioArray = respObject.getJSONArray("biometrics");
			List<MDSDeviceCaptureModel> lstBiometrics  = rCaptureModel.getLstBiometrics().get(type);
			if(lstBiometrics == null)
				lstBiometrics = new ArrayList<MDSDeviceCaptureModel>();
			rCaptureModel.getLstBiometrics().put(type, lstBiometrics);
			
			for(int i=0; i < bioArray.length(); i++) {
				JSONObject bioObject = bioArray.getJSONObject(i);
				String data = bioObject.getString("data");
				
				String hash = bioObject.getString("hash");
				JWTTokenModel jwtTok = new JWTTokenModel(data);
				JSONObject jsonPayload = new JSONObject(jwtTok.getJwtPayload());
				String jwtSign = jwtTok.getJwtSign();
				MDSDeviceCaptureModel model = new MDSDeviceCaptureModel();
				model.setBioType( CommonUtil.getJSONObjectAttribute(jsonPayload, "bioType",""));
				model.setBioSubType( CommonUtil.getJSONObjectAttribute(jsonPayload, "bioSubType",""));
				model.setQualityScore(CommonUtil.getJSONObjectAttribute(jsonPayload, "qualityScore",""));
				model.setBioValue ( CommonUtil.getJSONObjectAttribute(jsonPayload,"bioValue",""));
				model.setDeviceServiceVersion ( CommonUtil.getJSONObjectAttribute(jsonPayload,"deviceServiceVersion",""));
				model.setDeviceCode( CommonUtil.getJSONObjectAttribute(jsonPayload,"deviceCode",""));
				model.setHash(hash);
				if(mosipVersion!=null && mosipVersion.startsWith("1.2")) {
				model.setSb(jwtSign); // SB is signature block (header..signature)
				//String temp=jwtTok.getJwtPayload().replace(model.getBioValue(),);
				
				String BIOVALUE_KEY = "bioValue";
				String BIOVALUE_PLACEHOLDER = "\"<bioValue>\"";
				int bioValueKeyIndex = jwtTok.getJwtPayload().indexOf(BIOVALUE_KEY) + (BIOVALUE_KEY.length() + 1);
				int bioValueStartIndex = jwtTok.getJwtPayload().indexOf('"', bioValueKeyIndex);
				int bioValueEndIndex = jwtTok.getJwtPayload().indexOf('"', (bioValueStartIndex + 1));
				String bioValue = jwtTok.getJwtPayload().substring(bioValueStartIndex, (bioValueEndIndex + 1));
				String payload = jwtTok.getJwtPayload().replace(bioValue, BIOVALUE_PLACEHOLDER);
				model.setPayload(payload);
				}
				lstBiometrics.add(model);
				
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rCaptureModel;
	}
	
	
	public void setThresholdValue(String qualityScore) {

		String url = MDSURL + port + "/admin/score";
		JSONObject body = new JSONObject();
		body.put("type", "Biometric Device");
		body.put("qualityScore", qualityScore);
		body.put("fromIso", false);

		try {
			/*
			 * HttpRCapture capture = new HttpRCapture(url);
			 * capture.setMethod("SETTHRESHOLVALUE"); String response =
			 * RestClient.rawHttp(capture, body.toString()); JSONObject respObject = new
			 * JSONObject(response);
			 */
			
			Response response = given()
					.contentType(ContentType.JSON)
					.body(body.toString())
					.post(url );
			String resp = response.getBody().asString();
			System.out.println(resp);

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}

	}
	
	
		
	public static void main(String[] args) {
		
		MDSClient client = new MDSClient(0);
		//client.setProfile("res643726437264372");
		client.setProfile("Default");
		List<MDSDevice> d= client.getRegDeviceInfo("Iris");
		d.forEach( dv-> {
			System.out.println(dv.toJSONString());	
		});
		
		
		List<MDSDevice> f= client.getRegDeviceInfo("Finger");


		f.forEach( dv-> {
			System.out.println(dv.toJSONString());	
			
			MDSRCaptureModel r =  client.captureFromRegDevice(dv, null, "Finger",null,60,"1");
			//MDSRCaptureModel r =  client.captureFromRegDevice(d.get(0),null, "Iris",null,60,2);
		
			System.out.println( r.toJSONString());
			
		});
		
		//r = client.captureFromRegDevice(d.get(0),r, "Face",null,60,1);
		
		//System.out.println( r.toJSONString());
	}

	@Override
	public List<MDSDevice> getRegDeviceInfo(String type, String contextKey) {
		// TODO Auto-generated method stub
		return null;
	}


	
	
}
