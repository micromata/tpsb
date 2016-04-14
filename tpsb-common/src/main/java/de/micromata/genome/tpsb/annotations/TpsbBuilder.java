//
// Copyright (C) 2010-2016 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.tpsb.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author lado
 * @author roger
 * 
 * 
 * 
 * 
 *         Ein TestBuilder immer über eine TestApp-Klasse erzeugt. Intern hält der jeweilige TestBuilder auch eine Instanz einer TestApp.
 *         Der Unterschied zwischen einer TestApp und einem TestBuilder ist lediglich, dass ein TestBuilder nicht direkt erzeugt werden
 *         kann. 2.2.2 Schnittstellen getTestApp(): Gibt die Anwendung zurück. getX(): einfacher Getter setX(X x): einfacher Setter
 *         loadScenario(X scenario): Lädt Testdaten. List<X> listScenarios(): Wird intern ggf. verwendet um eine Auswahl zur Verfügung
 *         stehende Scenarios zur Verfügung zu stellen. enableMockupX(): Aktiviert einen Mockup. disableMockupX(): Deaktiviert einen Mockup.
 *         doX(): Löst eine Action aus. doX(Class target): Löst eine Aktion aus. Es wird erwartet, dass der Rückgabetyp des Aufrufes target
 *         class entspricht, der wiederum ein TestBuilder ist. Analog eines Form-Submits auf einer Seite. Falls die Action fehlschlägt - wie
 *         z.B. bei einem Valdierungsfehler, muss der TestBuilder eine Exception werfen, so dass der Testablauf unterbrochen wird.
 *         validateX(...): Validiert einen Aspekt. Da der TestBuilder den internen Status hält, kann z.B. geprüft werden, ob z.B. bei einer
 *         vorigen Aufrufs ein Validierungsfehler aufgetreten ist, die dem Anwender angezeigt werden. addComment("Comment"): Fügt ein
 *         Kommentar ein. Dieser wird im Testreport (und ggf. im Logging) mit ausgegeben. Ausserdem kann der Kommentar verwendet werden, um
 *         einen Test zu beschreiben. Sonstige Methoden Aus Komfortgründen ist es sinnvoll neben einfachen Methoden auch solche mit mehreren
 *         Parametern zu erlauben. 1 setUser("user").setPassword("password").doLogin(); 2 // versus 3 doLogin("user", "password");
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TpsbBuilder {

}
