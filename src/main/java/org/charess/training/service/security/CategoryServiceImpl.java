package org.charess.training.service.security;

import org.charess.training.domain.security.Audit;
import org.charess.training.domain.security.Category;
import org.charess.training.repository.security.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

    private UserService userService;
    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(UserService userService, CategoryRepository categoryRepository) {
        this.userService = userService;
        this.categoryRepository = categoryRepository;
    }

    public List<Category> list(){
        return categoryRepository.findAll();
    }

    public List<Category> search(String criteria){
        return (criteria==null || criteria.trim().isEmpty())?new ArrayList<>():categoryRepository.search(criteria);
    }

    public void delete(Integer id){
        categoryRepository.deleteById(id);
    }

    public Category save(Category place){
        Audit audit = place;
        userService.inject(audit);
        return (place==null || audit==null)?null:categoryRepository.save(place);
    }
}
