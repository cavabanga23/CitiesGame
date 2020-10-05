package com.ruslan.CityGame.Controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

@Controller
public class GameController {
    static char lastLetter;
    HashSet<String> usedWords = new HashSet<>();
    Set<String> cities = new HashSet<>();
    BufferedReader in = null;

    {
        try {
            in = new BufferedReader(new FileReader("src/main/resources/CitiesList.txt"));
            for (String city; (city = in.readLine()) != null; ) {
                cities.add(city);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void usersInput(String city, Model model) {
        lastLetter = getLastLetter(city);
        while (true) {
            Scanner sc = new Scanner(System.in);
            String newCity = sc.nextLine();
            wordTrueChecking(newCity, model);
            getAnswerCity(newCity, model);
        }
    }

    public void usersSecondInput(Model model) {
        while (true) {
            Scanner sc = new Scanner(System.in);
            String newCity = sc.nextLine();
            wordTrueChecking(newCity, model);
            getAnswerCity(newCity, model);
        }
    }

    @RequestMapping(value = "/next", method = RequestMethod.GET)
    public String getAnswerCity(@RequestParam(name = "word", required = true) String word, Model model) {
        String compWord = new String();
        String firstLetter = String.valueOf(getLastLetter(word));
        ArrayList<String> citiesList = new ArrayList<>(cities);
        for (String s : citiesList) {
            if (s.startsWith(firstLetter.toUpperCase())) {
                compWord = s;
                usedWords.add(compWord);
                cities.remove(compWord);
                break;
            }
        }
        lastLetter = getLastLetter(compWord);
        System.out.println(lastLetter);
        System.out.println("Вы ввели " + word + ", мой ход " + compWord + ", вам на букву " + lastLetter);

        model.addAttribute("compWord", compWord);
        return "cities";
    }

    public void wordTrueChecking(String word, Model model) {
        if (!cities.contains(word)) {
            System.out.println("Вы написали город, которого не существует, или город уже назван введите реальный город");
            usersSecondInput(model);
        } else {
            wordConsistChecking(word, model);
            cities.remove(word);
        }

    }

    public void wordConsistChecking(String word, Model model) {
        if (word.startsWith(String.valueOf(lastLetter))) {
            if (!usedWords.contains(word)) {
                usedWords.add(word);
            } else {
                System.out.println("Это слово уже использовано, попробуйте другое");
                usersSecondInput(model);
            }
        } else {
            System.out.println("Вы ввели город не на ту букву, введите другой ");
            usersSecondInput(model);
        }

        System.out.println(usedWords + " : эти слова уже назвали");
    }

    @RequestMapping(value = "/begin", method = RequestMethod.GET)
    public String getRandomCity(Model model) throws IOException {
        ArrayList<String> arrayList = new ArrayList<>(cities);
        String city = arrayList.get((int) (Math.random() * 1000));
        model.addAttribute("city", city);
        if (city.charAt(city.length() - 1) == 'ь' || city.charAt(city.length() - 1) == 'ы' || city.charAt(city.length() - 1) == 'ъ') {
            model.addAttribute("lastLetter", city.charAt(city.length() - 2));
        } else if (city.charAt(city.length() - 1) == 'й') {
            model.addAttribute("lastLetter", "И");
        } else
            model.addAttribute("lastLetter", city.charAt(city.length() - 1));
        setLastLetter(city);
        return "firstCity";
    }

    public char getLastLetter(String city) {
        int last = city.length() - 1;
        char lastChar = city.toUpperCase().charAt(last);
        if (city.toUpperCase().charAt(last) == 'Й') {
            lastChar = 'И';
        } else if (lastChar == 'Ь' || lastChar == 'Ы' || lastChar == 'Ъ') {
            lastChar = city.toUpperCase().charAt(last - 1);
        }
        return lastChar;
    }

    public void setLastLetter(String city) {
        char lastChar = getLastLetter(city);
        System.out.println(lastChar);
        lastLetter = lastChar;
    }

    @PostMapping("/begin/end")
    public String theEnd(@RequestParam String title, Model model){
        model.addAttribute("goodbye", "Спасибо за игру");
        return "firstCity";
    }
}
