package com.jpabook.controller;

import com.jpabook.domain.item.Book;
import com.jpabook.domain.item.Item;
import com.jpabook.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form) {
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }

    // 참고로 REST API로 만들때는 절대로 Entity를 넘겨줘서는 안된다.

    /**
     * 조회
     * @param model
     * @return
     */
    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    /**
     * 수정화면
     * @param itemId
     * @param model
     * @return
     */
    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm(); 
        form.setId(item.getId()); 
        form.setName(item.getName()); 
        form.setPrice(item.getPrice()); 
        form.setStockQuantity(item.getStockQuantity()); 
        form.setAuthor(item.getAuthor()); 
        form.setIsbn(item.getIsbn());
        
        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    /**
     * 수정 로직
     * @param itemId
     * @param form
     * @return
     */
    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") BookForm form) {
        // {itemId} 값으로 조작이 가능하기 때문에 보안을 위해 조심해야한다. (요즘은 session객체를 잘 쓰지 않는다.)

        Book book = new Book();
        book.setId(form.getId());
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";
    }

}
