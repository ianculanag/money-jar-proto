package com.psodex.rest.jar;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.psodex.rest.user.UserService;
import com.psodex.rest.util.IService;

@Service
public class JarService implements IService<Jar> {

	@Autowired
	JarRepository jarRepository;

	@Autowired
	UserService userService;

	@Override
	public List<Jar> findAll() {
		return jarRepository.findAllByUserId(userService.getLoggedInUserId());
	}

	@Override
	public Jar findById(int id) {
		return jarRepository.findByIdAndUserId(id, userService.getLoggedInUserId()).get();
	}

	@Override
	public void save(Jar jar) {
		setUser(jar);
		jarRepository.save(jar);

	}

	@Override
	public void update(Jar jar, int id) {
		setUser(jar);
		jar.setId(id);
		jarRepository.save(jar);
	}

	@Override
	public void delete(int id) {
		jarRepository.deleteById(id);
	}

	public void topUp(BigDecimal amount) {
		List<Jar> jars = findAll();
		jars.forEach(jar -> jar.setAvailableBalance(
				jar.getAvailableBalance().add(amount.multiply(BigDecimal.valueOf(jar.getPercentage() / 100)))));
		jars.forEach(jar -> jarRepository.save(jar));
	}

	public void withdrawBalance(BigDecimal amount, int jarId) {
		Jar jar = jarRepository.findById(jarId).orElse(null);
		if (jar != null) {
			jar.setAvailableBalance(jar.getAvailableBalance().subtract(amount));
		}
		jarRepository.save(jar);
	}

	public void setUser(Jar jar) {
		jar.setUser(userService.getLoggedInUser());
	}

	public void topUp(BigDecimal amount, boolean isFromView) {
		List<Jar> jars = findAll(isFromView);
		jars.forEach(jar -> jar.setAvailableBalance(
				jar.getAvailableBalance().add(amount.multiply(BigDecimal.valueOf(jar.getPercentage() / 100)))));
		jars.forEach(jar -> jarRepository.save(jar));
	}

	public List<Jar> findAll(boolean isFromView) {
		return jarRepository.findAllByUserId(userService.getLoggedInUser(isFromView).getId());
	}
}
