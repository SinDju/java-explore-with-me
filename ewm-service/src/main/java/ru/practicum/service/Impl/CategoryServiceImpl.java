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
import ru.practicum.model.Event;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventsRepository;

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
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        checkUniqNameCategory(newCategoryDto.getName());
        final Category category = CategoryMapper.toNewCategoryDto(newCategoryDto);
        final Category saveCategory = categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(saveCategory);
    }

    @Override
    public void deleteCategory(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new ObjectNotFoundException("Категории с ID " + catId + " не существует"));
        List<Event> events = eventsRepository.findByCategory(category);
        if (!events.isEmpty()) {
            throw new ConflictException("Cant delete category in use for some events");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        final Category oldCategory = categoryRepository.findById(catId).orElseThrow(() ->
                new ObjectNotFoundException("Категории с ID " + catId + " не существует"));
        if (categoryDto.getName() != null) {
            oldCategory.setName(categoryDto.getName());
        }
        final Category updatedCategory = categoryRepository.save(oldCategory);
        return CategoryMapper.toCategoryDto(updatedCategory);
    }

    private void checkUniqNameCategory(String name) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException(("Категории с названием " + name + " уже существует"));
        }
    }
}
