package com.example.service;

import com.example.data.entity.Customer;
import com.example.data.repository.CustomerRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll(){
        return customerRepository.findAll();
    }

    public List<Customer> search(String filter) {
        if (filter == null || filter.isBlank()) {
            return findAll();
        }

        return customerRepository.searchCustomers(filter.trim());
    }

    public void updateCustomer(Customer customer){
        customerRepository.save(customer);
    }

    public int deleteCustomer(Customer customer){
        try{
            customerRepository.delete(customer);
            return 0;
        }
        catch (DataIntegrityViolationException e) {
            return 1;
        }
    }

    public void addCustomer(Customer customer){
        customerRepository.save(customer);
    }

    public Page<Customer> getCustomersPage(int page, int size, String filter){
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.searchCustomers(filter, pageable);
    }

    public int getTotalPagesNumber(int pageSize, String filterText){
        Long totalRecords = 0L;

        if(filterText == null || filterText.isBlank()){
            totalRecords = customerRepository.count();
        }
        else{
            totalRecords = customerRepository.count(filterText);
        }

        if(totalRecords % pageSize == 0){
            return (int) (totalRecords / pageSize);
        }
        else{
            return (int) (totalRecords / pageSize) + 1;
        }
    }
}
