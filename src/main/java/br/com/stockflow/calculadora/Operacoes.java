package br.com.stockflow.calculadora;

import br.com.stockflow.exception.UnsupportedMathOperationException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

public class Operacoes {


    public Double soma(Double x, Double y)  {
        return x + y;
    }

    public Double subtracao(Double x, Double y)  {
        return x - y;
    }

    public Double divisao(Double x, Double y)  {
        return x / y;
    }

    public Double multiplicacao(Double x, Double y)  {
        return x * y;

    }

}



