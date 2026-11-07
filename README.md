# QM-P Boolean Simplifier

Truth Table Simplifier which combines the Quine-Mccluskey simplification algorithm with Pretricks method. GUI pre-fills inputs and allows you to directly create a truth table. Automatically simplifies any truth table input into it's simplest form.

**To use**
- Sliders control the number of inputs and outputs
- Inputs come prefilled, you only need to put in the output function
- x is for dont cares, not case sensitive
- anything that is not a 1 or x is treated as 0 (includes whitespace)
- Update table clears the table and changes it to whatever the new designated size is
- Save updates the csv file, **must be pressed before simplifying, otherwise there will be no table to simplify**
- Go! will automatically simplify and give the outputs in the output box
- Each output is designated by its column header, if there are no valid equations then "No Equations" will be displayed
