package ru.practicum.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageRequest)
                .stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new ObjectNotFoundException("Категории с ID " + catId + " не существует"));
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        if (!categoryRepository.existsByNameIgnoreCase(categoryDto.getName())) {
            throw new ConflictException("Категория с таким названием уже зарегестрирована");
        }
        Category category = categoryRepository.save(CategoryMapper.toCategory(categoryDto));
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public void deleteCategory(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new ObjectNotFoundException("Категория не найдена или недоступна");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDto) {
        Category oldCategory = categoryRepository.findById(catId).orElseThrow(() ->
                new ObjectNotFoundException("Категории с ID " + catId + " не существует"));
        oldCategory.setName(newCategoryDto.getName());
        return CategoryMapper.toCategoryDto(oldCategory);
    }
}
