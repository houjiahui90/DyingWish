/**
 * Copyright &copy; 2015-2020 <a href="http://www.nxzhxt.com/">nxzhxt</a> All rights reserved.
 */
package com.houjiahui.core.service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.houjiahui.DyingWish.entity.User;
import com.houjiahui.DyingWish.mapper.UserMapper;
import com.houjiahui.common.utils.UserUtils;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.houjiahui.common.config.Global;
import com.houjiahui.common.utils.CacheUtils;
import com.houjiahui.common.utils.Encodes;
import com.houjiahui.common.utils.StringUtils;
import com.houjiahui.core.persistence.Page;
import com.houjiahui.core.security.Digests;
import com.houjiahui.core.security.shiro.session.SessionDAO;
//import com.houjiahui.core.service.BaseService;
//import com.houjiahui.core.service.ServiceException;
//import com.houjiahui.modules.bind.entity.BindEntity;
//import com.houjiahui.modules.bind.mapper.BindMapper;
//import com.houjiahui.modules.database.entity.Parking;
//import com.houjiahui.modules.database.entity.Room;
//import com.houjiahui.modules.database.mapper.BuildingMapper;
//import com.houjiahui.modules.database.mapper.ParkingMapper;
//import com.houjiahui.modules.database.mapper.RoomMapper;
//import com.houjiahui.modules.database.mapper.VillageMapper;
//import com.houjiahui.modules.sys.entity.DataRule;
//import com.houjiahui.modules.sys.entity.Menu;
//import com.houjiahui.modules.sys.entity.Office;
//import com.houjiahui.modules.sys.entity.Role;
//import com.houjiahui.modules.sys.entity.User;
//import com.houjiahui.modules.sys.mapper.MenuMapper;
//import com.houjiahui.modules.sys.mapper.RoleMapper;
//import com.houjiahui.modules.sys.mapper.UserMapper;
//import com.houjiahui.modules.sys.utils.LogUtils;
//import com.houjiahui.modules.sys.utils.UserUtils;
//import com.houjiahui.modules.wechat.entity.WeChatUser;
//import com.houjiahui.modules.wechat.service.WeChatUserService;

/**
 * 系统管理，安全相关实体的管理类,包括用户、角色、菜单.
 * @author nxzhxt
 * @version 2016-12-05
 */
@Service
@Transactional(readOnly = true)
public class SystemService extends BaseService implements InitializingBean {

	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;
	public static final int SALT_SIZE = 8;

	@Autowired
	private DataRuleService dataRuleService;
	@Autowired
	private UserMapper userMapper;
//	@Autowired
//	private RoleMapper roleMapper;
//	@Autowired
//	private MenuMapper menuMapper;
	@Autowired
	private SessionDAO sessionDao;
//	@Autowired
//	private BindMapper bindMapper;
//	@Autowired
//	private VillageMapper villageMapper;
//	@Autowired
//	private BuildingMapper buildingMapper;
//	@Autowired
//	private RoomMapper roomMapper;
//	@Autowired
//	private ParkingMapper parkingMapper;
//	@Autowired
//	private WeChatUserService weChatUserService;
	public SessionDAO getSessionDao() {
		return sessionDao;
	}

//	@Autowired
//	private IdentityService identityService;

	//-- User Service --//

	/**
	 * 获取用户
	 * @param id
	 * @return
	 */
	public User getUser(String id) {
		return UserUtils.get(id);
	}

	/**
	 * 根据登录名获取用户
	 * @param loginName
	 * @return
	 */
	public User getUserByLoginName(String loginName) {
		return UserUtils.getByLoginName(loginName);
	}

	public Page<User> findUser(Page<User> page, User user) {
		dataRuleFilter(user);
		// 设置分页参数
		user.setPage(page);
		// 执行分页查询
		page.setList(userMapper.findList(user));
		return page;
	}

	/**
	 * 无分页查询人员列表
	 * @param user
	 * @return
	 */
	public List<User> findUser(User user){
		dataRuleFilter(user);
		List<User> list = userMapper.findList(user);
		return list;
	}

	/**
	 * 通过部门ID获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> findUserByOfficeId(String officeId) {
		List<User> list = (List<User>)CacheUtils.get(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + officeId);
//		if (list == null){
//			User user = new User();
//			user.setOffice(new Office(officeId));
//			list = userMapper.findUserByOfficeId(user);
//			CacheUtils.put(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + officeId, list);
//		}
		return list;
	}
	/**
	 *
	 * @return
	 */
	public List<User> findUserByPhone(String phone) {
		User user = new User();
		user.setPhone(phone);
		return userMapper.findUserByPhone(user);
	}

	@Transactional(readOnly = false)
	public void saveUser(User user) {
		if (StringUtils.isBlank(user.getId()) || user.getIsNewRecord()){
			user.preInsert();
			userMapper.insert(user);
		}else{
			// 清除原用户机构用户缓存
			User oldUser = userMapper.get(user.getId());
//			if (oldUser.getOffice() != null && oldUser.getOffice().getId() != null){
//				CacheUtils.remove(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + oldUser.getOffice().getId());
//			}
			// 更新用户数据
			user.preUpdate();
			userMapper.update(user);
		}
		if (StringUtils.isNotBlank(user.getId())){
			// 更新用户与角色关联
//			userMapper.deleteUserRole(user);
//			if (user.getRoleList() != null && user.getRoleList().size() > 0){
//				userMapper.insertUserRole(user);
//			}else{
//				throw new ServiceException(user.getLoginName() + "没有设置角色！");
//			}
			// 将当前用户同步到Activiti
//			saveActivitiUser(user);
			// 清除用户缓存
			UserUtils.clearCache(user);
//			// 清除权限缓存
//			systemRealm.clearAllCachedAuthorizationInfo();
		}
	}

	@Transactional(readOnly = false)
	public void updateUserInfo(User user) {
		user.preUpdate();
		userMapper.updateUserInfo(user);
		// 清除用户缓存
		UserUtils.clearCache(user);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
	}

	@Transactional(readOnly = false)
	public void deleteUser(User user) {
//		userMapper.deleteUserRole(user);
		userMapper.delete(user);
		// 同步到Activiti
//		deleteActivitiUser(user);
		// 清除用户缓存
		UserUtils.clearCache(user);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
	}

	@Transactional(readOnly = false)
	public void updatePasswordById(String id, String loginName, String newPassword) {
		User user = new User(id);
		user.setPassword(entryptPassword(newPassword));
		userMapper.updatePasswordById(user);
		// 清除用户缓存
		user.setLoginName(loginName);
		UserUtils.clearCache(user);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
	}

	@Transactional(readOnly = false)
	public void updateUserLoginInfo(User user) {
		// 保存上次登录信息
		user.setOldLoginIp(user.getLoginIp());
		user.setOldLoginDate(user.getLoginDate());
		// 更新本次登录信息
		user.setLoginIp(UserUtils.getSession().getHost());
		user.setLoginDate(new Date());
		userMapper.updateLoginInfo(user);
	}

	/**
	 * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
	 */
	public static String entryptPassword(String plainPassword) {
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
		return Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword);
	}

	/**
	 * 验证密码
	 * @param plainPassword 明文密码
	 * @param password 密文密码
	 * @return 验证成功返回true
	 */
	public static boolean validatePassword(String plainPassword, String password) {
		byte[] salt = Encodes.decodeHex(password.substring(0,16));
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
		return password.equals(Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword));
	}

	/**
	 * 获得活动会话
	 * @return
	 */
	public Collection<Session> getActiveSessions(){
		return sessionDao.getActiveSessions(false);
	}

	//-- Role Service --//

//	public Role getRole(String id) {
//		return roleMapper.get(id);
//	}
//
//	public Role getRoleByName(String name) {
//		Role r = new Role();
//		r.setName(name);
//		return roleMapper.getByName(r);
//	}
//
//	public Role getRoleByEnname(String enname) {
//		Role r = new Role();
//		r.setEnname(enname);
//		return roleMapper.getByEnname(r);
//	}
//
//	public List<Role> findRole(Role role){
//		return roleMapper.findList(role);
//	}
//
//	public List<Role> findAllRole(){
//		return UserUtils.getRoleList();
//	}

//	@Transactional(readOnly = false)
//	public void saveRole(Role role) {
//		if (StringUtils.isBlank(role.getId())){
//			role.preInsert();
//			roleMapper.insert(role);
//			// 同步到Activiti
//			saveActivitiGroup(role);
//		}else{
//			role.preUpdate();
//			roleMapper.update(role);
//		}
//		// 更新角色与菜单关联
//		roleMapper.deleteRoleMenu(role);
//		if (role.getMenuList().size() > 0){
//			roleMapper.insertRoleMenu(role);
//		}
//
//		// 更新角色与数据权限关联
//		roleMapper.deleteRoleDataRule(role);
//		if (role.getDataRuleList().size() > 0){
//			roleMapper.insertRoleDataRule(role);
//		}
//		// 同步到Activiti
//		saveActivitiGroup(role);
//		// 清除用户角色缓存
//		UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
////		// 清除权限缓存
////		systemRealm.clearAllCachedAuthorizationInfo();
//	}
//
//	@Transactional(readOnly = false)
//	public void deleteRole(Role role) {
//		roleMapper.deleteRoleMenu(role);
//		roleMapper.deleteRoleDataRule(role);
//		roleMapper.delete(role);
//		// 同步到Activiti
//		deleteActivitiGroup(role);
//		// 清除用户角色缓存
//		UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
////		// 清除权限缓存
////		systemRealm.clearAllCachedAuthorizationInfo();
//	}
//
//	@Transactional(readOnly = false)
//	public Boolean outUserInRole(Role role, User user) {
//		List<Role> roles = user.getRoleList();
//		for (Role e : roles){
//			if (e.getId().equals(role.getId())){
//				roles.remove(e);
//				saveUser(user);
//				return true;
//			}
//		}
//		return false;
//	}
//
//	@Transactional(readOnly = false)
//	public User assignUserToRole(Role role, User user) {
//		if (user == null){
//			return null;
//		}
//		List<String> roleIds = user.getRoleIdList();
//		if (roleIds.contains(role.getId())) {
//			return null;
//		}
//		user.getRoleList().add(role);
//		saveUser(user);
//		return user;
//	}
//
//	//-- Menu Service --//
//
//	public Menu getMenu(String id) {
//		return menuMapper.get(id);
//	}
//
//	public List<Menu> findAllMenu(){
//		return UserUtils.getMenuList();
//	}
//
//	public List<Menu> getChildren(String parentId){
//		return menuMapper.getChildren(parentId);
//	}
//
//	@Transactional(readOnly = false)
//	public void saveMenu(Menu menu) {
//
//		// 获取父节点实体
//		menu.setParent(this.getMenu(menu.getParent().getId()));
//
//		// 获取修改前的parentIds，用于更新子节点的parentIds
//		String oldParentIds = menu.getParentIds();
//
//		// 设置新的父节点串
//		menu.setParentIds(menu.getParent().getParentIds()+menu.getParent().getId()+",");
//
//		// 保存或更新实体
//		if (StringUtils.isBlank(menu.getId())){
//			menu.preInsert();
//			menuMapper.insert(menu);
//		}else{
//			menu.preUpdate();
//			menuMapper.update(menu);
//		}
//
//		// 更新子节点 parentIds
//		Menu m = new Menu();
//		m.setParentIds("%,"+menu.getId()+",%");
//		List<Menu> list = menuMapper.findByParentIdsLike(m);
//		for (Menu e : list){
//			e.setParentIds(e.getParentIds().replace(oldParentIds, menu.getParentIds()));
//			menuMapper.updateParentIds(e);
//		}
//		// 清除用户菜单缓存
//		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
////		// 清除权限缓存
////		systemRealm.clearAllCachedAuthorizationInfo();
//		// 清除日志相关缓存
//		CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
//	}
//
//	@Transactional(readOnly = false)
//	public void updateMenuSort(Menu menu) {
//		menuMapper.updateSort(menu);
//		// 清除用户菜单缓存
//		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
////		// 清除权限缓存
////		systemRealm.clearAllCachedAuthorizationInfo();
//		// 清除日志相关缓存
//		CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
//	}
//
//	@Transactional(readOnly = false)
//	public void deleteMenu(Menu menu) {
//
//		// 解除菜单角色关联
//		List<Object> mrlist =  menuMapper.execSelectSql(
//				"SELECT distinct a.menu_id as id FROM sys_role_menu a left join sys_menu menu on a.menu_id = menu.id WHERE a.menu_id ='"
//						+ menu.getId() + "' OR menu.parent_ids LIKE  '%," + menu.getId() + ",%'");
//		for (Object mr : mrlist) {
//			menuMapper.deleteMenuRole(mr.toString());
//		}
//
//		// 删除菜单关联的数据权限数据，以及解除角色数据权限关联
//		List<Object> mdlist = menuMapper.execSelectSql(
//				"SELECT distinct a.id as id FROM sys_datarule a left join sys_menu menu on a.menu_id = menu.id WHERE a.menu_id ='"
//						+ menu.getId() + "' OR menu.parent_ids LIKE  '%," + menu.getId() + ",%'");
//		for (Object md : mdlist) {
//			DataRule dataRule = new DataRule(md.toString());
//			dataRuleService.delete(dataRule);
//		}
//
//		menuMapper.delete(menu);
//		// 清除用户菜单缓存
//		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
//		// // 清除权限缓存
//		// systemRealm.clearAllCachedAuthorizationInfo();
//		// 清除日志相关缓存
//		CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
//	}

	/**
	 * 获取产品信息
	 */
	public static boolean printKeyLoadMessage(){
		StringBuilder sb = new StringBuilder();
		sb.append("...．．∵ ∴★．∴∵∴ ╭ ╯╭ ╯╭ ╯╭ ╯∴∵∴∵∴ \r\n ");
		sb.append("．☆．∵∴∵．∴∵∴▍▍ ▍▍ ▍▍ ▍▍☆ ★∵∴ \r\n ");
		sb.append("▍．∴∵∴∵．∴▅███████████☆ ★∵ \r\n ");
		sb.append("◥█▅▅▅▅███▅█▅█▅█▅█▅█▅███◤          欢迎使用 "+Global.getConfig("productName")+Global.getConfig("version")+"\r\n ");
		sb.append("． ◥███████████████████◤                    http://www.houjiahui.com\r\n ");
		sb.append(".．.．◥████████████████■◤\r\n ");
		System.out.println(sb.toString());
		return true;
	}

	///////////////// Synchronized to the Activiti //////////////////

	// 已废弃，同步见：ActGroupEntityServiceFactory.java、ActUserEntityServiceFactory.java

	/**
	 * 是需要同步Activiti数据，如果从未同步过，则同步数据。
	 */
	private static boolean isSynActivitiIndetity = true;
	public void afterPropertiesSet() throws Exception {
		if (!Global.isSynActivitiIndetity()){
			return;
		}
		if (isSynActivitiIndetity){
			isSynActivitiIndetity = false;
			// 同步角色数据
//			List<Group> groupList = identityService.createGroupQuery().list();
//			if (groupList.size() == 0){
//				Iterator<Role> roles = roleMapper.findAllList(new Role()).iterator();
//				while(roles.hasNext()) {
//					Role role = roles.next();
//					saveActivitiGroup(role);
//				}
//			}
//			// 同步用户数据
//			List<org.activiti.engine.identity.User> userList = identityService.createUserQuery().list();
//			if (userList.size() == 0){
//				Iterator<User> users = userMapper.findAllList(new User()).iterator();
//				while(users.hasNext()) {
//					saveActivitiUser(users.next());
//				}
//			}
		}
	}

//	private void saveActivitiGroup(Role role) {
//		if (!Global.isSynActivitiIndetity()){
//			return;
//		}
//		String groupId = role.getEnname();
//
//		// 如果修改了英文名，则删除原Activiti角色
//		if (StringUtils.isNotBlank(role.getOldEnname()) && !role.getOldEnname().equals(role.getEnname())){
//			identityService.deleteGroup(role.getOldEnname());
//		}
//
//		Group group = identityService.createGroupQuery().groupId(groupId).singleResult();
//		if (group == null) {
//			group = identityService.newGroup(groupId);
//		}
//		group.setName(role.getName());
//		group.setType(role.getRoleType());
//		identityService.saveGroup(group);
//
//		// 删除用户与用户组关系
//		List<org.activiti.engine.identity.User> activitiUserList = identityService.createUserQuery().memberOfGroup(groupId).list();
//		for (org.activiti.engine.identity.User activitiUser : activitiUserList){
//			identityService.deleteMembership(activitiUser.getId(), groupId);
//		}
//
//		// 创建用户与用户组关系
//		List<User> userList = findUser(new User(new Role(role.getId())));
//		for (User e : userList){
//			String userId = e.getLoginName();//ObjectUtils.toString(user.getId());
//			// 如果该用户不存在，则创建一个
//			org.activiti.engine.identity.User activitiUser = identityService.createUserQuery().userId(userId).singleResult();
//			if (activitiUser == null){
//				activitiUser = identityService.newUser(userId);
//				activitiUser.setFirstName(e.getName());
//				activitiUser.setLastName(StringUtils.EMPTY);
//				activitiUser.setEmail(e.getEmail());
//				activitiUser.setPassword(StringUtils.EMPTY);
//				identityService.saveUser(activitiUser);
//			}
//			identityService.createMembership(userId, groupId);
//		}
//	}
//
//	public void deleteActivitiGroup(Role role) {
//		if (!Global.isSynActivitiIndetity()){
//			return;
//		}
//		if(role!=null) {
//			String groupId = role.getEnname();
//			identityService.deleteGroup(groupId);
//		}
//	}
//
//	private void saveActivitiUser(User user) {
//		if (!Global.isSynActivitiIndetity()){
//			return;
//		}
//		String userId = user.getLoginName();//ObjectUtils.toString(user.getId());
//		org.activiti.engine.identity.User activitiUser = identityService.createUserQuery().userId(userId).singleResult();
//		if (activitiUser == null) {
//			activitiUser = identityService.newUser(userId);
//		}
//		activitiUser.setFirstName(user.getName());
//		activitiUser.setLastName(StringUtils.EMPTY);
//		activitiUser.setEmail(user.getEmail());
//		activitiUser.setPassword(StringUtils.EMPTY);
//		identityService.saveUser(activitiUser);
//
//		// 删除用户与用户组关系
//		List<Group> activitiGroups = identityService.createGroupQuery().groupMember(userId).list();
//		for (Group group : activitiGroups) {
//			identityService.deleteMembership(userId, group.getId());
//		}
//		// 创建用户与用户组关系
//		for (Role role : user.getRoleList()) {
//			String groupId = role.getEnname();
//			// 如果该用户组不存在，则创建一个
//			Group group = identityService.createGroupQuery().groupId(groupId).singleResult();
//			if(group == null) {
//				group = identityService.newGroup(groupId);
//				group.setName(role.getName());
//				group.setType(role.getRoleType());
//				identityService.saveGroup(group);
//			}
//			identityService.createMembership(userId, role.getEnname());
//		}
//	}
//
//	private void deleteActivitiUser(User user) {
//		if (!Global.isSynActivitiIndetity()){
//			return;
//		}
//		if(user!=null) {
//			String userId = user.getLoginName();//ObjectUtils.toString(user.getId());
//			identityService.deleteUser(userId);
//		}
//	}

	///////////////// Synchronized to the Activiti end //////////////////
	/**
	 * 更新房屋绑定信息
	 * @param userId
	 * @param roomId
	 * @param isDefault
	 * @return
	 */
//	@Transactional(readOnly = false)
//	public boolean bindRoom(String userId,String roomId,boolean isDefault) {
//		boolean isSuccess = true;
//		//获取房间对象
//		Room room = roomMapper.get(new Room(roomId));
//		User user = UserUtils.get(userId);
//		if (room == null || user == null) {
//			isSuccess = false;
//		}else {
//			BindEntity roomBind = bindMapper.getRoomBind(new BindEntity(userId, room));
//			if (roomBind == null) {
//				//创建房屋绑定关系
//				roomBind = new BindEntity();
//				roomBind.setUserId(userId);
//				roomBind.setRoomId(roomId);
//				//判断是否已经有默认房屋
//				BindEntity defaultRoomBind = bindMapper.getDefaultRoomBind(new BindEntity(userId));
//				if (defaultRoomBind == null) {
//					roomBind.setDefaultFlag("1");
//					isDefault = false;
//				}
//				bindMapper.insertRoomBind(roomBind);
//			}
//			BindEntity buildingBind = bindMapper.getBuildingBind(new BindEntity(userId, room));
//			if (buildingBind == null) {
//				buildingBind = new BindEntity();
//				buildingBind.setUserId(userId);;
//				buildingBind.setBuildingId(room.getBuildingId());
//				bindMapper.insertBuildingBind(buildingBind);
//			}
//			BindEntity villageBind = bindMapper.getVillageBind(new BindEntity(userId, room));
//			if (villageBind == null) {
//				villageBind = new BindEntity();
//				villageBind.setUserId(userId);
//				villageBind.setVillageId(room.getVillageId());
//				bindMapper.insertVillageBind(villageBind);
//			}
//			if (isDefault && !"1".equals(roomBind.getDefaultFlag())) {
//				//设置默认
//				roomBind.setDefaultFlag("1");
//				bindMapper.clearDefaultBindRoom(roomBind);
//				bindMapper.setDefaultBindRoom(roomBind);
//			}
//			isSuccess = true;
//		}
//		return isSuccess;
//	}
//	/**
//	 * 获取用户的房间绑定信息
//	 * @param userId
//	 * @return
//	 */
//	public Map<String, Object> getRoomBindInfo(String userId) {
//		Map<String, Object> map = new HashMap<>();
//		//判断是否已经有默认房屋,若没有默认房屋则没绑定房屋
//		BindEntity defaultRoomBind = bindMapper.getDefaultRoomBind(new BindEntity(userId));
//		if (defaultRoomBind != null) {
//			map.put("village", villageMapper.get(defaultRoomBind.getVillageId()));
//			map.put("building", buildingMapper.get(defaultRoomBind.getBuildingId()));
//			Room temp = roomMapper.get(new Room(defaultRoomBind.getRoomId()));
//			temp.setDefaultFlag("1");
//			map.put("room", temp);
//		}
//		return map;
//	}
//	/**
//	 * 获取用户的所有房间绑定信息
//	 * @param userId
//	 * @return
//	 */
//	public List<Room> getRoomBindInfoAll(String userId) {
//		Room room = new Room();
//		room.setUserId(userId);
//		return roomMapper.getRoomListByUserID(room);
//	}
//	/**
//	 * 移除用户绑定的房间
//	 * @param userId
//	 * @param roomId
//	 * @return
//	 */
//	@Transactional(readOnly = false)
//	public boolean removeRoomBind(String userId,String roomId) {
//		boolean isSuccess = true;
//		try {
//			//移除用户的房间绑定信息
//			BindEntity removeBind = bindMapper.getRoomBind(new BindEntity(userId, new Room(roomId)));
//			if (removeBind == null) {
//				return false;
//			}
//			//开始删除绑定关系
//			bindMapper.deleteRoomBind(removeBind);
//			//这里删除了不会立即生效,需要判断接下来查询的结果集是否有该绑定关系
//			//判断该用户是否还有别的绑定的房屋,若有指定第一个为默认房屋
//			List<BindEntity> list = bindMapper.getAllRoomBind(new BindEntity(userId));
////			boolean hasOther = list != null && list.size() > 0;
//			for (BindEntity temp : list) {
////				if (!removeBind.equals(temp)) {
////					hasOther = true;
//				temp.setDefaultFlag("1");
//				bindMapper.clearDefaultBindRoom(temp);
//				bindMapper.setDefaultBindRoom(temp);
////					this.bindRoom(userId, temp.getRoomId(), true);
//				break;
////				}
//			}
////			if (!hasOther) {
////				//删除相关的小区楼宇绑定信息
////				bindMapper.clearBuildingBind(userId);
////				bindMapper.clearVillageBind(userId);
////			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			isSuccess = false;
//		}
//		return isSuccess;
//	}
//	/**
//	 * 用户绑定车位
//	 * @param userId
//	 * @param parkingId
//	 * @return
//	 */
//	@Transactional(readOnly = false)
//	public boolean bindParking(String userId,String parkingId) {
//		boolean isSuccess = true;
//		//获取车位对象
//		Parking parking = parkingMapper.get(parkingId);
//		User user = UserUtils.get(userId);
//		if (parking == null || user == null) {
//			isSuccess = false;
//		}else {
//			BindEntity parkingBind = bindMapper.getParkingBind(new BindEntity(userId, parking.getId()));
//			if (parkingBind == null) {
//				//创建绑定关系
//				parkingBind = new BindEntity();
//				parkingBind.setUserId(userId);
//				parkingBind.setParkingId(parkingId);
//				bindMapper.insertParkingBind(parkingBind);
//			}
//			isSuccess = true;
//		}
//		return isSuccess;
//	}
//	@Transactional(readOnly = false)
//	public boolean removeParkingBind(String userId,String parkingId) {
//		boolean isSuccess = true;
//		try {
//			//移除用户的绑定信息
//			BindEntity removeBind = bindMapper.getParkingBind(new BindEntity(userId, parkingId));
//			if (removeBind == null) {
//				return false;
//			}
//			//开始删除绑定关系
//			bindMapper.deleteParkingBind(removeBind);
//		} catch (Exception e) {
//			e.printStackTrace();
//			isSuccess = false;
//		}
//		return isSuccess;
//	}
//	/**
//	 * 微信用户更新信息接口
//	 * @param jsonObject
//	 * @return
//	 */
//	@Transactional(readOnly = false)
//	public boolean updateUserInfo(JSONObject jsonObject) {
//		boolean isSusscess = false;
//		//手机号不能为空
//		String phone = jsonObject.getString("phone");
//		if (null == phone || "".equals(phone)) {
//			return false;
//		}
//		WeChatUser oldUser = weChatUserService.getUserByOpenId(jsonObject.getString("openid"));
//		if (oldUser == null) {
//			return false;
//		}
//		WeChatUser newUser = new WeChatUser(jsonObject);
//		try {
//			oldUser.updateData(newUser);
//			weChatUserService.save(oldUser);
//			isSusscess = true;
//		} catch (Exception e) {
//			isSusscess = false;
//		}
//		return isSusscess;
//	}
//	/**
//	 * 获取用户的所有车位绑定信息
//	 * @param userId
//	 * @return
//	 */
//	public List<Parking> getParkingBindInfoAll(String userId) {
//		return parkingMapper.getParkingListByUserID(userId);
//	}
}
