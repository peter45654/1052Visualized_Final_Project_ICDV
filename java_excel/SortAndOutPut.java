import org.json.*;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import java.util.Map;
import java.util.HashMap;
//import lib.compare.*;
public class SortAndOutPut
{
	private String temp;
	static JSONArray DataArr;
	static JSONObject outObj;
	static JSONArray outArr;
	private static String fileString;
	static String county="宜蘭縣";
	
	//read string from input file
	//source from :https://stackoverflow.com/questions/326390/how-do-i-create-a-java-string-from-the-contents-of-a-file
	private static String readFile(String file) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader (file));
    String         line = null;
    StringBuilder  stringBuilder = new StringBuilder();
    String         ls = System.getProperty("line.separator");
						//System.getProperty("line.separator");
						//https://stackoverflow.com/questions/36796136/difference-between-system-getpropertyline-separator-and-n

    try {
        while((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        return stringBuilder.toString();
    } finally {
        reader.close();
    }
}
	static protected void getFeatureCounty(){
		//輸出JSONObj用的list
		ArrayList<JSONObject> catchList = new ArrayList<JSONObject> ();
		//整理鄉鎮市重複字串用的List
		ArrayList<String> TownList = new ArrayList<String> ();
		JSONObject unitJsonObj = null;
		JSONArray temp=new JSONArray();
		for(int i=0;i< DataArr.length();i++){
			//從DataArray 裡抓按順序取一個元素並拿取裡面的地址
			unitJsonObj=DataArr.optJSONObject(i);
			String catchToken=unitJsonObj.get("國內地址").toString();
			//判斷是否為目標縣市
			if(catchToken.startsWith(county))
			{
			//抽出鄉鎮市->之後要統計的單位，不能重複
			String town=catchToken.substring(county.length(),county.length()+3);
			//判斷鄉鎮是否重複
			 Boolean repeat=false;
			for(int c=0;c<TownList.size();c++){
				String tempString=(String)TownList.get(c);
				if(tempString.equals(town)){repeat=true;break;}
				}
				if(!repeat){
					TownList.add(town);
				}
			//先將目標縣市的資料抓出來存到 catchList
			catchList.add(DataArr.optJSONObject(i));	
			}else{
				continue;
			}
		}
		//整理從含目標縣市JsonObj的資料要匯出成csv
		System.out.println("Sorting......");
		//這邊是雙層結構
		outObj=new JSONObject();
		JSONArray targetArray=new JSONArray();
		JSONObject insert=new JSONObject();
		//初始化 
		Map<String, Integer> CountTownMap = new HashMap<String, Integer>();
		
		for(int c=0;c<TownList.size();c++){
		String tempString=(String)TownList.get(c);
		CountTownMap.put( tempString, Integer.valueOf(0));
		}
		//初始化 統計各國家Map
		Map<String,Integer> CountNationMap = new HashMap<String,Integer>();
		for(int i=0;i<catchList.size();i++){
			unitJsonObj=catchList.get(i);
			String catchToken=unitJsonObj.get("國內地址").toString();
			String town=catchToken.substring(county.length(),county.length()+3);
			Object townO=town;
			//初始化 統計各國家Map
			String nationToken=unitJsonObj.get("國別").toString();
			CountNationMap.put(town+"_"+nationToken,Integer.valueOf(0));
		}
		for (Object key : CountTownMap.keySet()) {
            System.out.println(key + " : " + CountTownMap.get(key));
        }
		for (Object key : CountNationMap.keySet()) {
            System.out.println(key + " : " + CountNationMap.get(key));
        }	
		//一筆一筆抓取鄉鎮市標頭 先處理資料一遍
		for(int i=0;i<catchList.size();i++){
			unitJsonObj=catchList.get(i);
			String catchToken=unitJsonObj.get("國內地址").toString();
			String town=catchToken.substring(county.length(),county.length()+3);
			Object townO=town;
			//修改統計map的值
			int tempCount=CountTownMap.get(townO);
			tempCount+=1;
			CountTownMap.put(town,tempCount);
			//統計國家個數
			String nationToken=unitJsonObj.get("國別").toString();
			Object getKey= town+"_"+nationToken;
			int tempNationCount=CountNationMap.get(getKey);
			tempNationCount+=1;
			CountNationMap.put(town+"_"+nationToken,tempNationCount);
		}
		
		//一筆一筆抓取鄉鎮市標頭 將統計map 的值塞進output obj
		ArrayList<JSONObject> targetList=new ArrayList<JSONObject>();
		for(int i=0;i<TownList.size();i++){
			String town=TownList.get(i);
			Object townO=town;
			insert.put("鄉鎮市區",town);
			insert.put("所在縣市",county);
			insert.put("外商個數",CountTownMap.get(townO));
			//避免確認的國家值被其他鄉鎮市蓋掉
			ArrayList<String> avoidOverwrite=new ArrayList<String>();
			for (Object key : CountNationMap.keySet()) {
				String insertNation=key.toString().substring(town.length()+1);
				//確認是否包含
				Boolean includeThisNation=false;
				for(int c=0;c<avoidOverwrite.size();c++){
					if(avoidOverwrite.get(c).equals(insertNation))
					{
						includeThisNation=true;
					}
				}
				if(key.toString().startsWith(town))
				{
					Object getKey=town+"_"+insertNation;
					insert.put(insertNation,CountNationMap.get(getKey));
					avoidOverwrite.add(insertNation);
				}
				else if(!includeThisNation){
					insert.put(insertNation,0);
				}
			}
			//將單筆鄉鎮市資料塞進output
			targetArray.put(i,insert);
			System.out.println(insert);
			System.out.println();
			insert=new JSONObject();
		}
	
		outObj.put(county,targetArray);
		//System.out.println(outObj);
		System.out.println("Sorting Array size : "+catchList.size());
			for (Object key : CountNationMap.keySet()) {
            System.out.println(key + " : " + CountNationMap.get(key));
        }
		//System.out.println(outObj);
		System.out.println("Sorting Targer number : "+TownList.size());
		System.out.println("Sorting ended");
		
	}
    protected static void JSON2CSV(){
        String jsonString = "{\"infile\": [{\"field1\": 11,\"field2\": 12,\"field3\": 13},{\"field1\": 21,\"field2\": 22,\"field3\": 23},{\"field1\": 31,\"field2\": 32,\"field3\": 33}]}";

        JSONObject output= new JSONObject();
        try {
            //output = new JSONObject(jsonString);
			//output.put("infile",DataArr);

            JSONArray docs = outObj.getJSONArray(county);

            File file=new File(county+".csv");
            String csv = CDL.toString(docs);
            FileUtils.writeStringToFile(file, csv);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
    }


	public static void main(String[] args){
	//FDI_Outward
		try{
			fileString=readFile("Data.json");
			}catch(IOException e){
			  e.printStackTrace();
			}
		System.out.println("print fileString");
	    DataArr=new JSONArray(fileString);
		//System.out.println(fileString);
		getFeatureCounty();
		JSON2CSV ();
		System.out.println("end print");
		

	}
	
}

