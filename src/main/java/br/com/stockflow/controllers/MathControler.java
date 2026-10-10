package br.com.stockflow.controllers;


import br.com.stockflow.calculadora.Operacoes;
import br.com.stockflow.converter.NumberConverter;
import br.com.stockflow.exception.UnsupportedMathOperationException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calculadora")
public class MathControler {

    private Operacoes calculadora = new Operacoes();


    @RequestMapping("/soma/{x}/{y}")
    public Double soma(@PathVariable("x") String x,
                            @PathVariable("y") String y) throws  Exception {
        if (!NumberConverter.isNumeric(x) || !NumberConverter.isNumeric(y)) {
            throw new UnsupportedMathOperationException("Por favor, informe um valor numerico.");
        }
        return calculadora.soma(NumberConverter.convertToDouble(x) , NumberConverter.convertToDouble(y));

    }
    @RequestMapping("/subtracao/{x}/{y}")
    public Double subtracao(@PathVariable("x") String x,
                       @PathVariable("y") String y) throws  Exception {
        if (!NumberConverter.isNumeric(x) || !NumberConverter.isNumeric(y)) {
            throw new UnsupportedMathOperationException("Por favor, informe um valor numerico.");
        }
        return calculadora.subtracao(NumberConverter.convertToDouble(x) , NumberConverter.convertToDouble(y));

    }

    @RequestMapping("/divisao/{x}/{y}")
    public Double divisao(@PathVariable("x") String x,
                       @PathVariable("y") String y) throws  Exception {

        if (!NumberConverter.isNumeric(x) || !NumberConverter.isNumeric(y)) {
            throw new UnsupportedMathOperationException("Por favor, informe um valor numerico.");
        }
        return calculadora.divisao(NumberConverter.convertToDouble(x) , NumberConverter.convertToDouble(y));


    }

    @RequestMapping("/multipliacao/{x}/{y}")
    public Double multipliacao(@PathVariable("x") String x,
                       @PathVariable("y") String y) throws  Exception {

        if (!NumberConverter.isNumeric(x) || !NumberConverter.isNumeric(y)) {
            throw new UnsupportedMathOperationException("Por favor, informe um valor numerico.");
        }
        return calculadora.multiplicacao(NumberConverter.convertToDouble(x) , NumberConverter.convertToDouble(y));


    }




}




