package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dto.MemDTO;


/*
 * view 
 * 1. java +spring +jsp
 * 2. ajax + jsp
 * 3. angular, react, vue
 * 
 */



//http://www.json.org/json-en.html
//http://github.com/google/gson

@Controller
public class clientController {

	public clientController() {
		// TODO Auto-generated constructor stub
	}
	
//	gson 이용
//	http://localhost:8090/myclient/list.do
	@RequestMapping(value = "/list.do", method = RequestMethod.GET)
	public ModelAndView executeSelect(ModelAndView mav) throws IOException {
		
		URL url = new URL("http://localhost:8090/myapp/mem/list");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		if(conn.getResponseCode()==HttpURLConnection.HTTP_OK) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//			System.out.println(reader.readLine());
			
			List<MemDTO> alist = new ArrayList<MemDTO>();
			
//			google 꺼로 갖고 와라
			JsonElement element =JsonParser.parseReader(reader);
			JsonArray jsonlist = element.getAsJsonArray();
			
			Gson gson = new Gson();
			for(int i=0; i<jsonlist.size(); i++) {
				MemDTO dto = gson.fromJson(jsonlist.get(i), MemDTO.class);
				alist.add(dto);
			}
			
			reader.close();
			mav.addObject("alist", alist);
		}
		
		mav.setViewName("list");
		return mav;
	}
	
	
	
	
//	simple json... 전혀 심플하지 않다
//	http://localhost:8090/myclient/list2.do
	@RequestMapping(value = "/list2.do", method = RequestMethod.GET)
	public ModelAndView executeSelect2(ModelAndView mav) throws IOException, ParseException {
		
		URL url = new URL("http://localhost:8090/myapp/mem/list");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		if(conn.getResponseCode()==HttpURLConnection.HTTP_OK) {
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//			System.out.println(reader.readLine());
			
			List<MemDTO> alist = new ArrayList<MemDTO>();
			
			//json (simple)
			JSONParser parser = new JSONParser();
			JSONArray result = (JSONArray)parser.parse(reader);
//			System.out.println(result instanceof JSONArray);
//			System.out.println(result.get(0));
			
			for(int i=0; i<result.size(); i++) {
				JSONObject object = (JSONObject)result.get(i);
				
				MemDTO dto = new MemDTO();
				dto.setNum(Integer.parseInt(object.get("num").toString()));
//				dto.setName(object.get("name").toString());
				dto.setName((String)object.get("name"));
				dto.setAge(Integer.parseInt(object.get("age").toString()));
//				dto.setLoc(object.get("loc").toString());
				dto.setLoc((String)object.get("loc"));
				alist.add(dto);
			}
			
		mav.addObject("alist", alist);
		}
		
		mav.setViewName("list");
		return mav;
	}
	
	
	
//	http://localhost:8090/myclient/listone.do
	@RequestMapping(value = "/listone.do", method = RequestMethod.GET)
	public ModelAndView executeSelectOne(ModelAndView mav) throws IOException {
		
//		url과 리퀘스트 메소드 방식을 멤컨트롤에 있는 녀석으로 맞춰서 바꿔주면 update든 인서트든 삭제든 다 할 수 있더ㅏ
		URL url = new URL("http://localhost:8090/myapp/mem/list/" + 60);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		if(conn.getResponseCode()==HttpURLConnection.HTTP_OK) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//			System.out.println(reader.readLine());
			
			List<MemDTO> alist = new ArrayList<MemDTO>();
			

			JsonElement element =JsonParser.parseReader(reader);
			reader.close();
			JsonObject jsonlist = element.getAsJsonObject();
			
			Gson gson = new Gson();
			
				MemDTO dto = gson.fromJson(jsonlist, MemDTO.class);
			
			
			
			mav.addObject("dto", dto);
		}
		
		mav.setViewName("listone");
		return mav;
	}

	

//	http://localhost:8090/myclient/write.do
	@RequestMapping(value = "/write.do", method = RequestMethod.GET)
	public String writeForm()  {
	
		return "write";
	}// 페이지 호출
	
	
	@RequestMapping(value = "/write.do", method = RequestMethod.POST)
	public String writePro(MemDTO dto) throws IOException {
		URL url = new URL("http://localhost:8090/myapp/mem/insert");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		
		// 서버 요청 데이터를 json 타입으로 요청(requestbody에 전달시)
		conn.setRequestProperty("Content-Type", "application/json");
		
		//output 스트림으로 데이터를 넘겨준다는 의미
		conn.setDoOutput(true);
		
		Gson gson = new Gson();
		//{"num":0 ; "name": "default" ; "age" : 0 ; "loc" : "지역"}
		String json = gson.toJson(dto);
		System.out.println(json);
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
		bw.write(json);
		bw.close();
		
		
		int responseCode = conn.getResponseCode();
		System.out.println(responseCode);
		if(responseCode==200) {
			return "redirect:/list.do";
		}else {
			System.out.println(conn.getResponseMessage());
			return "redirect:/list.do";
		}
		
		
	}//////////////////추가, 삽입
	
	
	@RequestMapping(value = "/update.do", method = RequestMethod.GET)
	public String updateForm(@ModelAttribute("dto") MemDTO dto) {
		
		return "update";
	}//수정 수정 페이지 호출
	
	@RequestMapping(value = "/update.do", method = RequestMethod.POST)
	public String updatePro(MemDTO dto) throws IOException {
		URL url = new URL("http://localhost:8090/myapp/mem/update");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("PUT");
		
		// 서버 요청 데이터를 json 타입으로 요청(requestbody에 전달시)
		conn.setRequestProperty("Content-Type", "application/json");
		
		//output 스트림으로 데이터를 넘겨준다는 의미
		conn.setDoOutput(true);
		
		Gson gson = new Gson();
		//{"num":0 ; "name": "default" ; "age" : 0 ; "loc" : "지역"}
		String json = gson.toJson(dto);
		System.out.println(json);
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
		bw.write(json);
		bw.close();
		
		
		int responseCode = conn.getResponseCode();
		System.out.println(responseCode);
		if(responseCode==200) {
			return "redirect:/list.do";
		}else {
			System.out.println(conn.getResponseMessage());
			return "redirect:/update.do";
		}
		
		
	}//////////////////////////수정하는 행위
	

	
	
	@RequestMapping(value = "/delete.do", method = RequestMethod.GET)
	public String deletePro(int num) throws IOException {
		URL url = new URL("http://localhost:8090/myapp/mem/delete/"+num);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("DELETE");
		
		// 서버 요청 데이터를 json 타입으로 요청(requestbody에 전달시)
		conn.setRequestProperty("Content-Type", "application/json");
		
		//output 스트림으로 데이터를 넘겨준다는 의미
		conn.setDoOutput(false);
		
//		Gson gson = new Gson();
//		//{"num":0 ; "name": "default" ; "age" : 0 ; "loc" : "지역"}
//		String json = gson.toJson(num);
//		System.out.println(json);
//		
//		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
//		bw.write(json);
//		bw.close();
		
		
		int responseCode = conn.getResponseCode();
		System.out.println(responseCode);
		if(responseCode==200) {
			return "redirect:/list.do";
		}else {
			System.out.println(conn.getResponseMessage());
			return "redirect:/list.do";
		}
		
		
	}
	
	

}// class

