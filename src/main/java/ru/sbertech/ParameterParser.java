package ru.sbertech;

/**
 * Производит разбор параметра и конфигурирует Сканнер.
 * @author Dmitry Dobrynin
 *         Date: 10.11.11 time: 22:51
 */
public interface ParameterParser {
    boolean parse(String parameter, Scanner scanner);
}
