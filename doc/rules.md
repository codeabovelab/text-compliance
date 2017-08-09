# Rules
Texaco have a `com.codeabovelab.tpc.core.processor.Processor` which apply rules to Document. 
Each rule have follow properties:
 * ruleId - Id of rule. It must be unique for whole system.
 * weight - Used for order of rules before applying to document. Less weight rules apply first.
 * predicate - Predicate which determine of applicability of rule. It encoded as JSON Object. 
 * action - The code which will be executed when 'predicate' is triggered. It encoded as JSON Object. Allow null.
 * description - Human readable text about rule.
 * enabled - Boolean flag for enable/disable rule.
 * child - Boolean flag. It mean that rule is not applicable on top level, in other words 'child' rules has been applied
  only when it called from other rules. See example of 'multiRule'.
 
## Examples
```json
{
  "ruleId": "firstMyRule",
  "weight": 1,
  "predicate": "{\"@type\":\"TextClassifierPredicate\"}",
  "enabled": true,
  "child": true
}
```
```json
{
  "ruleId": "keywordsRule",
  "weight": 1,
  "predicate": "{\"@type\":\"WordPredicate\"}",
  "enabled": true,
  "child": true
}
```
And finally, rule which call above defined rules. 
```json
{
  "ruleId": "multiRule",
  "weight": 1,
  "predicate": "{\"@type\":\"RegexPredicate\",\"pattern\":\".*\"}",
  "action": "{\"@type\":\"ApplyRulesAction\",\"rulesNames\":[\"firstMyRule\", \"keywordsRule\"]}",
  "enabled": true,
  "child": false
}
```