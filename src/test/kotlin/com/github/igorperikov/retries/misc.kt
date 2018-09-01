package com.github.igorperikov.retries

open class ParentException : Exception()
class ChildException : ParentException()
