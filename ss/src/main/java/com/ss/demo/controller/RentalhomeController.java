package com.ss.demo.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ss.demo.domain.RentalhomeVO;
import com.ss.demo.domain.RoomVO;
import com.ss.demo.service.RentalhomeService;

@Controller
@RequestMapping(value="/rentalhome")
public class RentalhomeController {
	
	@Autowired
	private RentalhomeService rentalhomeService;
	
	@RequestMapping(value="/rentalhomeMain.do")
	public String rentalhomeMain(Model model){
		
		List<RentalhomeVO> list = rentalhomeService.selectAll();
		model.addAttribute("list", list);
		
//		System.out.println(list.get(list.size()-1).getLogical_name());
		return "rentalhome/rentalhomeMain";
	}
	
	@RequestMapping(value="/rentalhomeView.do")
	public String rentalhomeView(Model model, int rentalhome_idx){
		RentalhomeVO rentalhomeVO = rentalhomeService.selectOneByIdx(rentalhome_idx);
		model.addAttribute("rentalhomeVO", rentalhomeVO);
		
		List<RentalhomeVO> list_rentalhome_attach = rentalhomeService.selectAll_rentalhome_attach(rentalhome_idx);
		model.addAttribute("attach", list_rentalhome_attach);

		List<RoomVO> list_rentalhome_room_attach = rentalhomeService.selectAll_room_attach();
		model.addAttribute("attach_room", list_rentalhome_room_attach);
		
		List<RoomVO> list = rentalhomeService.selectAll_room(rentalhome_idx);
		model.addAttribute("list", list);
		return "rentalhome/rentalhomeView";
	}

	@RequestMapping(value="/rentalhomeWrite.do", method=RequestMethod.GET)
	public String rentalhomeWrite(){
		
		return "rentalhome/rentalhomeWrite";
	}

	@RequestMapping(value="/rentalhomeWrite.do", method=RequestMethod.POST)
	public String rentalhomeWrite(
			RentalhomeVO rentalhomeVO,
			@RequestParam("multiFile") List<MultipartFile> multiFileList,
			HttpServletRequest request
			){
		
		rentalhomeVO.setuNo(1);
		int value = rentalhomeService.insert(rentalhomeVO);
		
		// 받아온것 출력 확인
		System.out.println("multiFileList : " + multiFileList);
		
		// path 가져오기
		String path = request.getSession().getServletContext().getRealPath("resources/upload");
		
		File file = new File(path);
		// 경로에 폴더가 없으면 폴더를 생성
		if(!file.exists()){
			file.mkdirs();
		}
		
		List<Map<String, String>> fileList = new ArrayList<>();
		
		for(int i = 0; i < multiFileList.size(); i++) {
			String originFile = multiFileList.get(i).getOriginalFilename();
			String etc = originFile.substring(originFile.lastIndexOf("."));
			String changeFile = UUID.randomUUID().toString() +  originFile;
			
			Map<String, String> map = new HashMap<>();
			map.put("originFile", originFile);
			map.put("changeFile", changeFile);
			
			fileList.add(map);
		}
		
		System.out.println("fileList: " + fileList);
		boolean flag = true;
		// 파일업로드
		try {
			for(int i = 0; i < multiFileList.size(); i++) {
				File uploadFile = new File(path + "\\" + fileList.get(i).get("changeFile"));
				multiFileList.get(i).transferTo(uploadFile);
				
				rentalhomeVO.setLogical_name(fileList.get(i).get("originFile"));
				rentalhomeVO.setPhysical_name(fileList.get(i).get("changeFile"));
				
				if(flag) {
					rentalhomeVO.setThumbnail("Y");
					flag = false;
				}else {
					rentalhomeVO.setThumbnail("N");
				}
				System.out.println("rentalhomeVO 썸네일: " + rentalhomeVO.getThumbnail());
				int value2 = rentalhomeService.insert_file(rentalhomeVO);
			}
			
			System.out.println("다중 파일 업로드 성공!");
			
		} catch (Exception e) {
			System.out.println("다중 파일 업로드 실패");
			// 만약 업로드 실패하면 파일 삭제
			for(int i = 0; i < multiFileList.size(); i++) {
				new File(path + "\\" + fileList.get(i).get("changeFile")).delete();
			}
			
			e.printStackTrace();
		}
		
		return "redirect:/rentalhome/rentalhomeMain.do";
	}

	@RequestMapping(value="/rentalhomeModify.do", method=RequestMethod.GET)
	public String rentalhomeModify(int rentalhome_idx, Model model){
		
		RentalhomeVO rentalhomeVO = rentalhomeService.selectOneByIdx(rentalhome_idx);
		model.addAttribute("rentalhomeVO", rentalhomeVO);
		
		List<RentalhomeVO> list_rentalhome_attach = rentalhomeService.selectAll_rentalhome_attach(rentalhome_idx);
		model.addAttribute("attach", list_rentalhome_attach);
		
		
		return "rentalhome/rentalhomeModify";
	}
	
	@RequestMapping(value="/rentalhomeModify.do", method=RequestMethod.POST)
	public String rentalhomeModify(
			RentalhomeVO rentalhomeVO,
			@RequestParam("multiFile") List<MultipartFile> multiFileList,
			HttpServletRequest request){
		
		int value = rentalhomeService.update(rentalhomeVO);
		
		// 받아온것 출력 확인
		System.out.println("multiFileList : " + multiFileList);
		
		// path 가져오기
		String path = request.getSession().getServletContext().getRealPath("resources/upload");
		
		File file = new File(path);
		// 경로에 폴더가 없으면 폴더를 생성
		if(!file.exists()){
			file.mkdirs();
		}
		
		List<Map<String, String>> fileList = new ArrayList<>();
		
		for(int i = 0; i < multiFileList.size(); i++) {
			String originFile = multiFileList.get(i).getOriginalFilename();
			String etc = originFile.substring(originFile.lastIndexOf("."));
			String changeFile = UUID.randomUUID().toString() +  originFile;
			
			Map<String, String> map = new HashMap<>();
			map.put("originFile", originFile);
			map.put("changeFile", changeFile);
			
			fileList.add(map);
		}
		
		System.out.println("fileList: " + fileList);
		// 파일업로드
		try {
			for(int i = 0; i < multiFileList.size(); i++) {
				File uploadFile = new File(path + "\\" + fileList.get(i).get("changeFile"));
				multiFileList.get(i).transferTo(uploadFile);
				
				rentalhomeVO.setLogical_name(fileList.get(i).get("originFile"));
				rentalhomeVO.setPhysical_name(fileList.get(i).get("changeFile"));
				rentalhomeVO.setThumbnail("N");
				System.out.println("rentalhomeVO 썸네일: " + rentalhomeVO.getThumbnail());
				
				int value2 = rentalhomeService.insert_file(rentalhomeVO);
			}
			System.out.println("다중 파일 업로드 성공!");
			
		} catch (Exception e) {
			System.out.println("다중 파일 업로드 실패");
			// 만약 업로드 실패하면 파일 삭제
			for(int i = 0; i < multiFileList.size(); i++) {
				new File(path + "\\" + fileList.get(i).get("changeFile")).delete();
			}
			
			e.printStackTrace();
		}
		return "redirect:/rentalhome/rentalhomeView.do?rentalhome_idx="+rentalhomeVO.getRentalhome_idx();
	}

	@RequestMapping(value="/rentalhomeDelete.do", method=RequestMethod.POST)
	public String rentalhomeDelete(int rentalhome_idx){
		
		int value = rentalhomeService.delete_rentalhome(rentalhome_idx);
		return "redirect:/rentalhome/rentalhomeMain.do";
	}

	@RequestMapping(value="/rentalhomeWrite_room.do", method=RequestMethod.GET)
	public String rentalhomeWrite_room(int rentalhome_idx, Model model){
		RentalhomeVO rentalhomeVO = rentalhomeService.selectOneByIdx(rentalhome_idx);
		model.addAttribute("rentalhomeVO", rentalhomeVO);
		return "rentalhome/rentalhomeWrite_room";
	}

	@RequestMapping(value="/rentalhomeWrite_room.do", method=RequestMethod.POST)
	public String rentalhomeWrite_room(
			RoomVO roomVO,
			@RequestParam("multiFile") List<MultipartFile> multiFileList,
			HttpServletRequest request){
		int value = rentalhomeService.insert_room(roomVO);
		
		// 받아온것 출력 확인
		System.out.println("multiFileList : " + multiFileList);
		
		// path 가져오기
		String path = request.getSession().getServletContext().getRealPath("resources/upload");
		
		File file = new File(path);
		// 경로에 폴더가 없으면 폴더를 생성
		if(!file.exists()){
			file.mkdirs();
		}
		
		List<Map<String, String>> fileList = new ArrayList<>();
		
		for(int i = 0; i < multiFileList.size(); i++) {
			String originFile = multiFileList.get(i).getOriginalFilename();
			String etc = originFile.substring(originFile.lastIndexOf("."));
			String changeFile =UUID.randomUUID().toString() + originFile;
			
			Map<String, String> map = new HashMap<>();
			map.put("originFile", originFile);
			map.put("changeFile", changeFile);
			
			fileList.add(map);
		}
		
		System.out.println("fileList: " + fileList);
		boolean flag = true;
		// 파일업로드
		try {
			for(int i = 0; i < multiFileList.size(); i++) {
				File uploadFile = new File(path + "\\" + fileList.get(i).get("changeFile"));
				multiFileList.get(i).transferTo(uploadFile);
				
				roomVO.setLogical_name(fileList.get(i).get("originFile"));
				roomVO.setPhysical_name(fileList.get(i).get("changeFile"));
				
				if(flag) {
					roomVO.setThumbnail("Y");
					flag = false;
				}else {
					roomVO.setThumbnail("N");
				}
				System.out.println("roomVO 썸네일: " + roomVO.getThumbnail());
				int value2 = rentalhomeService.insert_room_file(roomVO);
			}
			
			System.out.println("다중 파일 업로드 성공!");
			
		} catch (Exception e) {
			System.out.println("다중 파일 업로드 실패");
			// 만약 업로드 실패하면 파일 삭제
			for(int i = 0; i < multiFileList.size(); i++) {
				new File(path + "\\" + fileList.get(i).get("changeFile")).delete();
			}
			
			e.printStackTrace();
		}
		
		return "redirect:/rentalhome/rentalhomeView.do?rentalhome_idx="+roomVO.getRentalhome_idx();
	}

	@RequestMapping(value="/rentalhomeModify_room.do", method=RequestMethod.GET)
	public String rentalhomeModify_room(RoomVO roomVO, Model model){
		RentalhomeVO rentalhomeVO = rentalhomeService.selectOneByIdx(roomVO.getRentalhome_idx());
		model.addAttribute("rentalhomeVO", rentalhomeVO);
		
		RoomVO roomVO_ = rentalhomeService.selectOneByIdx_room(roomVO.getRoom_idx());
		model.addAttribute("roomVO", roomVO_);
		return "rentalhome/rentalhomeModify_room";
	}
	
	@RequestMapping(value="/rentalhomeModify_room.do", method=RequestMethod.POST)
	public String rentalhomeModify_room(RoomVO roomVO){
		System.out.println(roomVO.toString());
		int value = rentalhomeService.update_room(roomVO);
		
		return "redirect:/rentalhome/rentalhomeView.do?rentalhome_idx="+roomVO.getRentalhome_idx();
	}

	@RequestMapping(value="/rentalhomeDelete_room.do", method=RequestMethod.POST)
	public String rentalhomeDelete_room(RoomVO roomVO){
		int value = rentalhomeService.delete_room(roomVO.getRoom_idx());
		
		return "redirect:/rentalhome/rentalhomeView.do?rentalhome_idx="+roomVO.getRentalhome_idx();
	}
	
	@RequestMapping(value="/rentalhomeThumbnail.do", method=RequestMethod.POST)
	public String rentalhomeThumbnail(RentalhomeVO rentalhomeVO){
		
		int value1 = rentalhomeService.init_attach_thumbmail(rentalhomeVO.getRentalhome_idx());
		
		int value2 = rentalhomeService.attach_thumbmail(rentalhomeVO.getAttach_idx());
		
		
		return "redirect:/rentalhome/rentalhomeView.do?rentalhome_idx="+rentalhomeVO.getRentalhome_idx();
	}

	@RequestMapping(value="/rentalhomeDelete_attach.do", method=RequestMethod.POST)
	public String rentalhomeDelete_room_attach(int attach_idx, int rentalhome_idx){
		
		int value = rentalhomeService.delete_attach(attach_idx);
		
		return "redirect:/rentalhome/rentalhomeView.do?rentalhome_idx="+rentalhome_idx;
	}
	
	@RequestMapping(value="/rentalhomeReserve.do", method=RequestMethod.GET)
	public String rentalhomeReserve(){
		
		return "rentalhome/rentalhomeReserve";
	}
	
}
