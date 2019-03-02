# Parser

Stuff we learn from observing `Global`

## Positions

In `Vector()` versus `Vector.apply()` the information about the implied `apply` seems
only visible through heuristics on the position of the `Select` tree. First case

    RangePosition(<console>, 13, 13, 19)
                                 ^

Second case

    RangePosition(<console>, 13, 20, 25)
                                 ^

So from `start == point` we can determine that the name is actually invisible.

__No:__ That's not the case. Because `Vector` is `Select(Select(Ident(scala),Name(package)),Name(Vector))`, and here
`Name(Vector)` also has that property.
